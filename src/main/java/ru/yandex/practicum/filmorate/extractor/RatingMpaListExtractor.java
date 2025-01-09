package ru.yandex.practicum.filmorate.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@Component
public class RatingMpaListExtractor implements ResultSetExtractor<List<RatingMpa>> {

    @Override
    public List<RatingMpa> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<RatingMpa> mpa = new LinkedList<>();
        RatingMpa ratingMpa;

        while (rs.next()) {
            ratingMpa = new RatingMpa();
            ratingMpa.setId(rs.getInt("id"));
            ratingMpa.setName(rs.getString("point_name"));
            mpa.add(ratingMpa);
        }

        return mpa;
    }
}
