package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.extractor.GenreExtractor;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements SimpleRepository<Genre> {
    private final NamedParameterJdbcOperations jdbcGenres;

    @Autowired
    private GenreExtractor genreExtractor;

    private static final String FIND_GENRE_BY_ID = "SELECT * FROM genre WHERE id = :id";
    private static final String GET_ALL_GENRES_ID = "SELECT id FROM genre";

    @Override
    public Collection<Genre> getAll() {
        List<Integer> genresId = jdbcGenres.getJdbcOperations().queryForList(GET_ALL_GENRES_ID, Integer.class);

        return genresId.stream()
                .map(this::findById)
                .map(Optional::get)
                .toList();
    }

    @Override
    public Optional<Genre> findById(Integer id) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("id", id);
        return Optional.ofNullable(jdbcGenres.query(FIND_GENRE_BY_ID, mapSqlParameterSource, genreExtractor));
    }
}
