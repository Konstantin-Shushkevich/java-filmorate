package ru.yandex.practicum.filmorate.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RatingMpaExtractor implements ResultSetExtractor<RatingMpa> {

    @Override
    public RatingMpa extractData(ResultSet rs) throws SQLException, DataAccessException {
        RatingMpa ratingMpa = new RatingMpa();

        while (rs.next()) {
            ratingMpa.setId(rs.getInt("id"));
            ratingMpa.setName(rs.getString("point_name"));
        }

        if (ratingMpa.getId() == 0) {
            return null;
        }

        return ratingMpa;
    }
}
