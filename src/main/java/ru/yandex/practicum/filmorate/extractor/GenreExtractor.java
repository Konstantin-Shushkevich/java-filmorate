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
        Genre genre = null;

        while (rs.next()) {
            genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("genre_name"));
        }

        return genre;
    }
}
