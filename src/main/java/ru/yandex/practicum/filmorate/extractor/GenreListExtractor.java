package ru.yandex.practicum.filmorate.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@Component
public class GenreListExtractor implements ResultSetExtractor<List<Genre>> {

    @Override
    public List<Genre> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Genre> genres = new LinkedList<>();
        Genre genre;

        while (rs.next()) {
            genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("genre_name"));
            genres.add(genre);
        }

        return genres;
    }
}
