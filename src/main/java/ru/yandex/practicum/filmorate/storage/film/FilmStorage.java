package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Film saveFilm(Film film);

    Film updateFilm(Film film);

    Film deleteFilm(Integer id);

    Collection<Film> getAll();

    Optional<Film> findById(Integer id);
}
