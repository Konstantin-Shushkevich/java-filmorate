package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

    public Film modifyFilm(Film film) {
        filmRepository.findById(film.getId()).orElseThrow(() ->
                new NotFoundException("Film's id doesn't in database"));

        return filmRepository.updateFilm(film);
    }

    public Film removeFilm(Integer id) {
        filmRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Film's id doesn't in database"));

        return filmRepository.deleteFilm(id);
    }

    public Film likeFilm(Integer id, Integer userId) {
        Film film = filmRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Film's id doesn't in database"));
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User's id doesn't in database"));
        log.trace("The film and the user are in database. Start of adding like...");

        film.addLike(userId);
        log.trace("Like from user with id: {} had been put", userId);
        return filmRepository.likeFilm(id, userId).orElseThrow();
    }

    public Film disLikeFilm(Integer id, Integer userId) {
        Film film = filmRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Film's id doesn't in database"));
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User's id doesn't in database"));
        log.trace("The film and the user are in database. Start of deleting like...");

        film.delLike(userId);

        return filmRepository.disLikeFilm(id, userId).orElseThrow();
    }

    public List<Film> getTopChart(Integer count) {
        log.trace("Getting topChart in progress");

        return filmRepository.getAll().stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count).collect(Collectors.toList());
    }
}
