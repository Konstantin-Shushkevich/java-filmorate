package ru.yandex.practicum.filmorate.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

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
        Set<Integer> genres = new LinkedHashSet<>();
        Set<Integer> likes = new HashSet<>();

        while (rs.next()) {
            if (film.getId() == null) {
                film.setId(rs.getInt("id"));
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(LocalDate.parse(rs.getString("release_date")));
                film.setDuration(rs.getInt("duration"));
                film.setRatingMPA(rs.getInt("mpa_rating_id"));
            }
            genres.add(rs.getInt("genre_id"));
            likes.add(rs.getInt("user_id"));
        }

        if (!genres.isEmpty()) {
            film.setGenres(genres);
        }

        if (!(likes.isEmpty() || likes.contains(0))) {
            film.setLikes(likes);
        }

        if (film.getId() == null) {
            return null;
        }

        return film;
    }
}
