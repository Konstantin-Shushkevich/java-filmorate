package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    public Film likeFilm(Integer id, Integer userId) {
        Film film = inMemoryFilmStorage.findById(id).orElseThrow(() ->
                new NotFoundException("Film's id doesn't in database"));
        inMemoryUserStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("User's id doesn't in database"));
        log.trace("The film and the user are in database. Start of adding like...");

        film.addLike(userId);
        inMemoryFilmStorage.updateFilm(film);
        log.trace("Like from user with id: {} had been put", userId);
        return film;
    }

    public Film disLikeFilm(Integer id, Integer userId) {
        Film film = inMemoryFilmStorage.findById(id).orElseThrow(() ->
                new NotFoundException("Film's id doesn't in database"));
        inMemoryUserStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("User's id doesn't in database"));
        log.trace("The film and the user are in database. Start of deleting like...");

        film.delLike(userId);
        inMemoryFilmStorage.updateFilm(film);
        log.trace("Like from user with id: {} had been deleted", userId);
        return film;
    }

    public List<Film> getTopChart(int count) {
        log.trace("Getting topChart in progress");
        return inMemoryFilmStorage.getTopChart(count);
    }
}
