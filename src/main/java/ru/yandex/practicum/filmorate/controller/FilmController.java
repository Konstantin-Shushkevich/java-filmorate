package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Validated
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(setNextId());
        films.put(film.getId(), film);
        log.debug("Film {} was successfully added. Film id in database is: {}", film.getName(), film.getId());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (film.getId() == null) {
            log.error("Missing id");
            throw new ValidationException("Trying to update film with missing id");
        } else if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Film {} was successfully updated", film.getName());
            return film;
        } else {
            log.debug("Film {} was not updated: wasn't added before", film.getName());
            throw new ValidationException("Trying to update ашдь, that hadn't been added before");
        }
    }

    private int setNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
