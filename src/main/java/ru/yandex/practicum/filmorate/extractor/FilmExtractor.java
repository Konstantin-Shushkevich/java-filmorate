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
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Component
public class FilmExtractor implements ResultSetExtractor<Film> {

    @Override
    public Film extractData(ResultSet rs) throws SQLException, DataAccessException {
        Film film = null;
        Set<Genre> genres = new TreeSet<>((g1, g2) -> Integer.compare(g1.getId(), g2.getId()));
        Set<Integer> likes = new HashSet<>();
        RatingMpa ratingMpa = new RatingMpa();

        while (rs.next()) {
            if (film == null) {
                film = new Film();
                film.setId(rs.getInt("id"));
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(LocalDate.parse(rs.getString("release_date")));
                film.setDuration(rs.getInt("duration"));
            }

            ratingMpa.setId(rs.getInt("mpa_rating_id"));
            ratingMpa.setName(rs.getString("point_name"));

            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("genre_name"));

            if (genre.getName() != null) {
                genres.add(genre);
            }

            likes.add(rs.getInt("user_id"));
        }

        if (film == null) {
            throw new BadRequestException("Something went wrong. Film is null");
        }

        if (!likes.contains(0)) {
            film.setLikes(likes);
        }

        if (!genres.isEmpty()) {
            film.setGenres(genres);
        }

        if (ratingMpa.getName() != null) {
            film.setMpa(ratingMpa);
        }

        return film;
    }
}
