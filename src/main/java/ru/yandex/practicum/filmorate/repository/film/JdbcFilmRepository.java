package ru.yandex.practicum.filmorate.repository.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.extractor.FilmExtractor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcFilmRepository implements FilmRepository {

    private final NamedParameterJdbcOperations jdbcFilms;

    @Autowired
    private FilmExtractor filmExtractor;

    private static final String INSERT_FILM_TO_FILMS =
            "INSERT INTO films (name, description, release_date, " +
                    "duration, mpa_rating_id) " +
                    "VALUES (:name, :description, :release_date, :duration, :mpa_rating_id)";

    private static final String INSERT_VALUES_TO_FILM_GENRE =
            "INSERT INTO film_genre (film_id, genre_id) " +
                    "VALUES (:film_id, :genre_id)";

    private static final String INSERT_FILM_TO_FILMS_IF_UPDATE =
            "INSERT INTO films (id, name, description, " +
                    "release_date, duration, mpa_rating_id) " +
                    "VALUES (:id, :name, :description, :release_date, :duration, :mpa_rating_id)";

    private static final String DELETE_FILM_FROM_FILMS = "DELETE FROM films WHERE id = :id";

    private static final String GET_ALL_ID_FROM_FILMS = "SELECT id FROM films";

    private static final String GET_VALUES_FOR_FILM_MAPPING =
            "SELECT f.*, fg.genre_id, g.genre_name, mr.point_name, l.user_id " +
                    "FROM films f JOIN film_genre fg ON f.id = fg.film_id " +
                    "JOIN genre g ON fg.genre_id = g.id " +
                    "JOIN mpa_rating mr ON mr.id = f.mpa_rating_id " +
                    "LEFT JOIN likes l ON l.film_id = f.id " +
                    "WHERE f.id = :id";

    private static final String INSERT_LIKE_VALUES_TO_LIKES =
            "INSERT INTO likes (user_id, film_id) " +
                    "VALUES (:user_id, :film_id)";

    private static final String DELETE_LIKE_FROM_LIKES =
            "DELETE FROM likes " +
                    "WHERE (user_id = :user_id AND film_id =:film_id)";

    private static final String GET_ALL_ID_FROM_MPA_RATING = "SELECT id FROM mpa_rating";

    private static final String GET_ALL_ID_FROM_GENRE = "SELECT id FROM genre";

    @Override
    public Film saveFilm(Film film) {
        int mpaId = film.getMpa().getId();

        validateRatingMpa(mpaId);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("name", film.getName());
        mapSqlParameterSource.addValue("description", film.getDescription());
        mapSqlParameterSource.addValue("release_date", film.getReleaseDate());
        mapSqlParameterSource.addValue("duration", film.getDuration());
        mapSqlParameterSource.addValue("mpa_rating_id", mpaId);

        jdbcFilms.update(INSERT_FILM_TO_FILMS, mapSqlParameterSource, keyHolder);

        int filmId = 0;

        if (keyHolder.getKey() != null) {
            filmId = keyHolder.getKey().intValue();
            film.setId(filmId);
        }

        saveFilmGenres(film);
        log.debug("Film: {} was successfully added. Film id in database is: {}", film.getName(), film.getId());

        return findById(filmId).orElseThrow(() -> new NotFoundException("Film's id doesn't in database"));
    }

    @Override
    public Film updateFilm(Film film) { // TODO
        Integer id = film.getId();
        findById(id).orElseThrow(() -> new NotFoundException("Film's id doesn't in database"));

        if (id == null) {
            throw new InternalServerException("Film's id is null");
        }

        deleteFilm(id);

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("id", id);
        mapSqlParameterSource.addValue("name", film.getName());
        mapSqlParameterSource.addValue("description", film.getDescription());
        mapSqlParameterSource.addValue("release_date", film.getReleaseDate());
        mapSqlParameterSource.addValue("duration", film.getDuration());
        mapSqlParameterSource.addValue("mpa_rating_id", film.getMpa().getId());

        jdbcFilms.update(INSERT_FILM_TO_FILMS_IF_UPDATE, mapSqlParameterSource);

        saveFilmGenres(film);
        log.debug("Film: {} was successfully updated", film.getName());

        return film;
    }

    @Override
    public Film deleteFilm(Integer id) {
        Film film = findById(id).orElseThrow(() -> new NotFoundException("Film's id doesn't in database"));
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("id", id);

        jdbcFilms.update(DELETE_FILM_FROM_FILMS, mapSqlParameterSource);
        log.debug("Film was deleted");

        return film;
    }

    @Override
    public Collection<Film> getAll() {

        List<Integer> filmsId = jdbcFilms.getJdbcOperations().queryForList(GET_ALL_ID_FROM_FILMS, Integer.class);
        List<Film> films = new LinkedList<>();

        for (Integer id : filmsId) {
            Film film = findById(id).orElseThrow(() -> new NotFoundException("Film's id doesn't in database"));
            films.add(film);
        }

        return films;
    }

    @Override
    public Optional<Film> findById(Integer id) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("id", id);

        return Optional.ofNullable(jdbcFilms.query(GET_VALUES_FOR_FILM_MAPPING, mapSqlParameterSource, filmExtractor));
    }

    @Override
    public Optional<Film> likeFilm(Integer id, Integer userId) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("user_id", userId);
        mapSqlParameterSource.addValue("film_id", id);
        jdbcFilms.update(INSERT_LIKE_VALUES_TO_LIKES, mapSqlParameterSource);
        return findById(id);
    }

    @Override
    public Optional<Film> disLikeFilm(Integer id, Integer userId) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("user_id", userId);
        mapSqlParameterSource.addValue("film_id", id);
        jdbcFilms.update(DELETE_LIKE_FROM_LIKES, mapSqlParameterSource);
        return findById(id);
    }

    private void saveFilmGenres(Film film) {
        if (!film.getGenres().isEmpty() && film.getId() != 0) {

            List<Integer> genresId = film.getGenres().stream()
                    .map(Genre::getId)
                    .toList();

            for (Integer id : genresId) {
                validateGenres(id);

                MapSqlParameterSource otherMapSqlParameterSource = new MapSqlParameterSource();
                otherMapSqlParameterSource.addValue("film_id", film.getId());
                otherMapSqlParameterSource.addValue("genre_id", id);

                jdbcFilms.update(INSERT_VALUES_TO_FILM_GENRE, otherMapSqlParameterSource);
            }
        }
    }

    private void validateRatingMpa(int mpaId) {
        List<Integer> ratingsIdFromRepository =
                jdbcFilms.getJdbcOperations().queryForList(GET_ALL_ID_FROM_MPA_RATING, Integer.class);

        if (!ratingsIdFromRepository.contains(mpaId)) {
            throw new InternalServerException("Not able to add film. Incorrect mpa");
        }
    }

    private void validateGenres(int id) {
        List<Integer> genresIdFromRepository =
                jdbcFilms.getJdbcOperations().queryForList(GET_ALL_ID_FROM_GENRE, Integer.class);

        if (!genresIdFromRepository.contains(id)) {
            throw new InternalServerException("Not able to add genre. Incorrect genre");
        }
    }
}
