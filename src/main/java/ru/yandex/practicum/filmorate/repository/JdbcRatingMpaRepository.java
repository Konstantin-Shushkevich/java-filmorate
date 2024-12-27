package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.extractor.RatingMpaExtractor;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.util.constant.RatingMpaRepositoryConstants.FIND_RATE_BY_ID;
import static ru.yandex.practicum.filmorate.util.constant.RatingMpaRepositoryConstants.GET_ALL_RATES_ID;

@Repository
@RequiredArgsConstructor
public class JdbcRatingMpaRepository implements SimpleRepository<RatingMpa> {
    private final NamedParameterJdbcOperations jdbcRatingMpa;

    @Autowired
    private RatingMpaExtractor ratingMpaExtractor;


    @Override
    public Collection<RatingMpa> getAll() {
        List<Integer> ratingsId = jdbcRatingMpa.getJdbcOperations().queryForList(GET_ALL_RATES_ID, Integer.class);
        return ratingsId.stream()
                .map(this::findById)
                .map(Optional::get)
                .toList();
    }

    @Override
    public Optional<RatingMpa> findById(Integer id) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("id", id);
        return Optional.ofNullable(jdbcRatingMpa.query(FIND_RATE_BY_ID, mapSqlParameterSource, ratingMpaExtractor));
    }
}
