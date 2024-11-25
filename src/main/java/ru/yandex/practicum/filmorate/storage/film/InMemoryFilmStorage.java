package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Set<Film> topChartedFilms = new TreeSet<>(comparator);

    private static final Comparator<Film> comparator = (film1, film2) -> {
        return film2.getLikes().size() - film1.getLikes().size();
    };

    @Override
    public Film saveFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.debug("Film: {} was successfully added. Film id in database is: {}", film.getName(), film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            addToTopChart(film);
            films.put(film.getId(), film);
            log.debug("Film: {} was successfully updated", film.getName());
            return film;
        } else {
            log.warn("Film: {} was not updated: hadn't been added before", film.getName());
            throw new NotFoundException("Trying to update film, that hadn't been added before");
        }
    }

    @Override
    public Film deleteFilm(Integer id) {
        if (films.containsKey(id)) {
            Film film = films.remove(id);
            delFromTopChart(film);
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

    private void addToTopChart(Film film) {
        Integer id = film.getId();

        topChartedFilms.remove(findById(id).get());
        log.trace("Old version of film was deleted from topChartedFilms for the correct work of program");

        if (!film.getLikes().isEmpty()) {
            topChartedFilms.add(film);
            log.debug("Film was successfully added to topChartedFilms");
        }
    }

    private void delFromTopChart(Film film) {
        topChartedFilms.remove(film);
    }

    public List<Film> getTopChart(Integer count) {
        if (topChartedFilms.size() < count) {
            return new ArrayList<>(topChartedFilms);
        } else {
            return new ArrayList<>(topChartedFilms).subList(0, count);
        }
    }

    private int getNextId() {
        return films.keySet()
                .stream()
                .max(Integer::compare)
                .orElse(0) + 1;
    }
}
