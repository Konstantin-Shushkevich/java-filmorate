package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.extractor.GenreExtractor;
import ru.yandex.practicum.filmorate.extractor.GenreListExtractor;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.util.constant.GenreRepositoryConstants.*;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements SimpleRepository<Genre> {

    private final NamedParameterJdbcOperations jdbcGenres;
    private final GenreExtractor genreExtractor;
    private final GenreListExtractor genreListExtractor;

    @Override
    public Collection<Genre> getAll() {
        return jdbcGenres.query(GET_ALL_VALUES_FROM_GENRE, genreListExtractor);
    }

    @Override
    public Optional<Genre> findById(Integer id) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("id", id);
        return Optional.ofNullable(jdbcGenres.query(FIND_GENRE_BY_ID, mapSqlParameterSource, genreExtractor));
    }
}
