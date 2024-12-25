package ru.yandex.practicum.filmorate.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenreExtractor implements ResultSetExtractor<Genre> {
    @Override
    public Genre extractData(ResultSet rs) throws SQLException, DataAccessException {
        Genre genre = new Genre();

        while (rs.next()) {
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("genre_name"));
        }

        if (genre.getId() == 0) {
            return null;
        }

        return genre;
    }
}
