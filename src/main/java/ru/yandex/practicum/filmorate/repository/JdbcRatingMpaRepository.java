package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.extractor.RatingMpaExtractor;
import ru.yandex.practicum.filmorate.extractor.RatingMpaListExtractor;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.Collection;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.util.constant.RatingMpaRepositoryConstants.*;

@Repository
@RequiredArgsConstructor
public class JdbcRatingMpaRepository implements SimpleRepository<RatingMpa> {

    private final NamedParameterJdbcOperations jdbcRatingMpa;
    private final RatingMpaExtractor ratingMpaExtractor;
    private final RatingMpaListExtractor ratingMpaListExtractor;

    @Override
    public Collection<RatingMpa> getAll() {
        return jdbcRatingMpa.query(GET_ALL_VALUES_FROM_MPA_RATING, ratingMpaListExtractor);
    }

    @Override
    public Optional<RatingMpa> findById(Integer id) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("id", id);
        return Optional.ofNullable(jdbcRatingMpa.query(FIND_RATE_BY_ID, mapSqlParameterSource, ratingMpaExtractor));
    }
}
