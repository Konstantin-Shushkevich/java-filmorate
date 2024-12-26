package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.film.JdbcFilmRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final JdbcFilmRepository jdbcFilmRepository;

    public Film modifyFilm(Film film) {
        jdbcFilmRepository.findById(film.getId()).orElseThrow(() ->
                new NotFoundException("Film's id doesn't in database"));

        return jdbcFilmRepository.updateFilm(film);
    }

    public Film removeFilm(Integer id) {
        jdbcFilmRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Film's id doesn't in database"));

        return jdbcFilmRepository.deleteFilm(id);
    }

    public Film likeFilm(Integer id, Integer userId) {
        Film film = jdbcFilmRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Film's id doesn't in database"));
        jdbcFilmRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User's id doesn't in database"));
        log.trace("The film and the user are in database. Start of adding like...");

        film.addLike(userId);
        log.trace("Like from user with id: {} had been put", userId);
        return jdbcFilmRepository.likeFilm(id, userId).orElseThrow();
    }

    public Film disLikeFilm(Integer id, Integer userId) {
        Film film = jdbcFilmRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Film's id doesn't in database"));
        jdbcFilmRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User's id doesn't in database"));
        log.trace("The film and the user are in database. Start of deleting like...");

        film.delLike(userId);

        return jdbcFilmRepository.disLikeFilm(id, userId).orElseThrow();

    }

    public List<Film> getTopChart(Integer count) {
        log.trace("Getting topChart in progress");

        return jdbcFilmRepository.getAll().stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count).collect(Collectors.toList());
    }
}
