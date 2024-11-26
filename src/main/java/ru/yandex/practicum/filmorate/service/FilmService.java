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
    private final Set<Film> topChartedFilms = new TreeSet<>(comparator);

    private static final Comparator<Film> comparator = (film1, film2) -> {
        return film2.getLikes().size() - film1.getLikes().size();
    };

    public Film modifyFilm(Film film) {
        Film filmChecked = inMemoryFilmStorage.findById(film.getId()).orElseThrow(() ->
                new NotFoundException("Film's id doesn't in database"));

        delFromTopChart(filmChecked);
        addToTopChart(film);

        return inMemoryFilmStorage.updateFilm(film);
    }

    public Film removeFilm(Integer id) {
        Film filmChecked = inMemoryFilmStorage.findById(id).orElseThrow(() ->
                new NotFoundException("Film's id doesn't in database"));

        delFromTopChart(filmChecked);

        return inMemoryFilmStorage.deleteFilm(id);
    }

    public Film likeFilm(Integer id, Integer userId) {
        Film film = inMemoryFilmStorage.findById(id).orElseThrow(() ->
                new NotFoundException("Film's id doesn't in database"));
        inMemoryUserStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("User's id doesn't in database"));
        log.trace("The film and the user are in database. Start of adding like...");

        delFromTopChart(film);
        film.addLike(userId);
        addToTopChart(film);
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

        delFromTopChart(film);
        film.delLike(userId);
        addToTopChart(film);
        inMemoryFilmStorage.updateFilm(film);
        log.trace("Like from user with id: {} had been deleted", userId);
        return film;
    }

    private void addToTopChart(Film film) {
        if (!film.getLikes().isEmpty()) {
            topChartedFilms.add(film);
            log.debug("Film was successfully added to topChartedFilms");
        }
    }

    private void delFromTopChart(Film film) {
        topChartedFilms.remove(film);
        log.debug("Film was successfully deleted from topChartedFilms");
    }

    public List<Film> getTopChart(Integer count) {
        log.trace("Getting topChart in progress");
        if (topChartedFilms.size() < count) {
            return new ArrayList<>(topChartedFilms);
        } else {
            return new ArrayList<>(topChartedFilms).subList(0, count);
        }
    }
}
