package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.film.JdbcFilmRepository;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Validated
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final JdbcFilmRepository jdbcFilmRepository;

    @GetMapping
    public Collection<Film> getAll() {
        log.trace("Getting list of all films has been started");
        return jdbcFilmRepository.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        log.trace("Adding film is started");
        return jdbcFilmRepository.saveFilm(film);
    }

    @PutMapping
    @Validated(NotNull.class)
    public Film update(@Valid @RequestBody Film film) {
        log.trace("Updating film has been started");
        return filmService.modifyFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) {
        log.trace("Searching for film in progress");
        return jdbcFilmRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Film wasn't found"));
    }

    @DeleteMapping("/{id}")
    public Film deleteFilm(@PathVariable Integer id) {
        log.trace("Searching for film to delete in progress");
        return filmService.removeFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        log.trace("Process of liking has been started :-)");
        return filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film disLikeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        log.trace("Process of disliking has been started :-(");
        return filmService.disLikeFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopChart(@RequestParam(defaultValue = "10") Integer count) {
        log.trace("Getting top chart in progress");
        return filmService.getTopChart(count);
    }
}
