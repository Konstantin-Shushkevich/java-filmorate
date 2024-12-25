package ru.yandex.practicum.filmorate.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class FilmExtractor implements ResultSetExtractor<Film> {
    @Override
    public Film extractData(ResultSet rs) throws SQLException, DataAccessException {
        Film film = new Film();
        Set<Genre> genres = new LinkedHashSet<>();
        Set<Integer> likes = new HashSet<>();
        RatingMpa ratingMpa = new RatingMpa();

        while (rs.next()) {
            if (film.getId() == null) {
                film.setId(rs.getInt("id"));
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(LocalDate.parse(rs.getString("release_date")));
                film.setDuration(rs.getInt("duration"));
            }

            ratingMpa.setId(rs.getInt("mpa_rating_id"));
            ratingMpa.setName(rs.getString("point_name"));

            if (ratingMpa.getName() == null) { // TODO
                film.setMpa(ratingMpa);
            }

            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("genre_name"));

            if (genre.getName() != null) {
                genres.add(genre);
            }

            likes.add(rs.getInt("user_id"));
        }

        if (film.getId() == null) {
            throw new DataRetrievalFailureException("Something went wrong. Not able to get film");
        }

        if (!(likes.isEmpty() || likes.contains(0))) {
            film.setLikes(likes);
        }

        film.setGenres(genres);
        film.setMpa(ratingMpa);

        return film;
    }
}
