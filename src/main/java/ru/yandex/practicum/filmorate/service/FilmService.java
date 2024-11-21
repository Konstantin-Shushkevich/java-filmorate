package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.util.FilmRatingComparator;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    public Film likeFilm(Integer id, Integer userId) {
        Optional<Film> filmForLike = inMemoryFilmStorage.findById(id);

        if (filmForLike.isPresent() && inMemoryUserStorage.findById(userId).isPresent()) {
            filmForLike.get().getLikes().add(userId);
            inMemoryFilmStorage.putFilm(filmForLike.get());
            return filmForLike.get();
        }

        log.error("There is no film or user in database for likeFilm operation");
        throw new NotFoundException("Exception in process of trying to put like");
    }

    public Film disLikeFilm(Integer id, Integer userId) {
        Optional<Film> disLikedFilm = inMemoryFilmStorage.findById(id);

        if (disLikedFilm.isPresent() && inMemoryUserStorage.findById(userId).isPresent()) {
            disLikedFilm.get().getLikes().remove(userId);
            inMemoryFilmStorage.putFilm(disLikedFilm.get());
            return disLikedFilm.get();
        }

        log.error("There is no film or user in database for disLikeFilm operation");
        throw new NotFoundException("Exception in process of trying to delete like");
    }

    public List<Film> getTopChart(int count) {
        List<Film> topChartedFilms = calculateTopChart();

        if (topChartedFilms.size() < count) {
            return topChartedFilms;
        } else {
            return topChartedFilms.subList(0, count);
        }
    }

    private List<Film> calculateTopChart() {
        List<Film> films = new ArrayList<>(inMemoryFilmStorage.getAll());
        FilmRatingComparator filmRatingComparator = new FilmRatingComparator();

        films.sort(filmRatingComparator);

        return films;
    }
}
