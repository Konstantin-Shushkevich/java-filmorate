package ru.yandex.practicum.filmorate.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
public class FilmListExtractor implements ResultSetExtractor<List<Film>> {

    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {

        List<Film> films = new LinkedList<>();
        int currentId = 0;
        Film film = null;
        Set<Genre> genres = new TreeSet<>((g1, g2) -> Integer.compare(g1.getId(), g2.getId()));
        Set<Integer> likes = new HashSet<>();
        RatingMpa ratingMpa;

        while (rs.next()) {
            if (rs.getInt("id") != currentId) {

                if (film != null) {
                    film.setGenres(genres);
                    film.setLikes(likes);
                    films.add(film);
                }

                genres = new TreeSet<>((g1, g2) -> Integer.compare(g1.getId(), g2.getId()));
                likes = new HashSet<>();
                ratingMpa = new RatingMpa();
                film = new Film();

                film.setId(rs.getInt("id"));
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(LocalDate.parse(rs.getString("release_date")));
                film.setDuration(rs.getInt("duration"));
                ratingMpa.setId(rs.getInt("mpa_rating_id"));
                ratingMpa.setName(rs.getString("point_name"));

                if (ratingMpa.getName() != null) {
                    film.setMpa(ratingMpa);
                }
            }

            if (film == null) {
                throw new BadRequestException("Something went wrong. Film is null");
            }

            currentId = film.getId();

            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("genre_name"));

            if (genre.getName() != null) {
                genres.add(genre);
            }

            if (rs.getInt("user_id") != 0) {
                likes.add(rs.getInt("user_id"));
            }
        }

        if (film != null) {
            film.setGenres(genres);
            film.setLikes(likes);
            films.add(film);
        }

        return films;
    }
}
