package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film postFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.debug("Film: {} was successfully added. Film id in database is: {}", film.getName(), film.getId());
        log.debug("Film: {} was added to topCharted films", film.getName());
        return film;
    }

    @Override
    public Film putFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Film: {} was successfully updated", film.getName());
            return film;
        } else {
            log.warn("Film: {} was not updated: hadn't been added before", film.getName());
            throw new ValidationException("Trying to update film, that hadn't been added before");
        }
    }

    @Override
    public Film deleteFilm(Integer id) {
        if (films.containsKey(id)) {
            Film film = films.remove(id);
            log.debug("Film was deleted");
            return film;
        } else {
            log.warn("Film with id: {} was not deleted as it hadn't been added before", id);
            throw new ValidationException("Trying to delete film, that hadn't been added before");
        }
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Optional<Film> findById(Integer id) {
        return films.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(id))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    private int getNextId() {
        return films.keySet()
                .stream()
                .max(Integer::compare)
                .orElse(0) + 1;
    }
}
