package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmServiceTest {
    InMemoryFilmStorage inMemoryFilmStorageTest;
    InMemoryUserStorage inMemoryUserStorageTest;
    FilmService filmServiceTest;

    User user;
    User otherUser;
    Film film;
    Film otherFilm;

    @BeforeEach
    public void initialiseTestSpace() {
        inMemoryFilmStorageTest = new InMemoryFilmStorage();
        inMemoryUserStorageTest = new InMemoryUserStorage();
        filmServiceTest = new FilmService(inMemoryFilmStorageTest, inMemoryUserStorageTest);

        user = new User("user@test.com", "user", LocalDate.of(1990, 1, 1));
        otherUser = new User("otherUser@test.com", "otherUser",
                LocalDate.of(2000, 1, 1));
        film = new Film("film", "film for test", LocalDate.of(1960, 2, 2),
                120);
        otherFilm = new Film("otherFilm", "otherFilm for test",
                LocalDate.of(1970, 3, 3), 180);

        inMemoryFilmStorageTest.saveFilm(film);
        inMemoryFilmStorageTest.saveFilm(otherFilm);
        inMemoryUserStorageTest.saveUser(user);
        inMemoryUserStorageTest.saveUser(otherUser);
    }

    @Test
    void shouldAddAndDeleteFilmByLikesToTopChart() {
        Integer filmId = film.getId();
        Integer userId = user.getId();
        Integer otherUserId = otherUser.getId();

        filmServiceTest.likeFilm(filmId, userId);
        List<Film> topChart = filmServiceTest.getTopChart(10);

        assertTrue(topChart.contains(film) && topChart.size() == 1,
                "Adding film to topChart doesn't work correctly");

        filmServiceTest.likeFilm(filmId, otherUserId);
        topChart = filmServiceTest.getTopChart(10);
        assertTrue(topChart.contains(film) && topChart.size() == 1,
                "Incorrect work of program after adding one more like");

        filmServiceTest.disLikeFilm(filmId, userId);
        topChart = filmServiceTest.getTopChart(10);
        assertTrue(topChart.contains(film) && topChart.size() == 1,
                "Incorrect work of program after deletion 1 of 2 likes");

        filmServiceTest.disLikeFilm(filmId, otherUserId);
        topChart = filmServiceTest.getTopChart(10);
        assertTrue(!topChart.contains(film) && topChart.isEmpty(),
                "Incorrect work of program after deletion all likes");
    }

    @Test
    void shouldRemoveFilmFromTopChartAfterDeletion() {
        Integer filmId = film.getId();
        Integer userId = user.getId();

        filmServiceTest.likeFilm(filmId, userId);
        filmServiceTest.removeFilm(filmId);
        List<Film> topChart = filmServiceTest.getTopChart(10);

        assertEquals(0, topChart.size(), "Film is still in topChart after deleting from data base");
    }

    @Test
    void shouldDeleteOldVersionInUpdatingAndAddIfNecessary() {
        Integer filmId = film.getId();
        Integer userId = user.getId();
        Integer otherUserId = otherUser.getId();
        Film filmUpdated = new Film("film updated", "film for test updated",
                LocalDate.of(1960, 2, 2), 120);
        filmUpdated.setId(filmId);
        filmUpdated.setLikes(Set.of(userId, otherUserId));

        filmServiceTest.likeFilm(filmId, userId);
        filmServiceTest.modifyFilm(filmUpdated);

        List<Film> topChart = filmServiceTest.getTopChart(10);
        assertTrue(topChart.contains(filmUpdated) && topChart.size() == 1,
                "Updated film has 2 likes. It had to replace old version of film in topChart");

        film.setLikes(Set.of());
        filmServiceTest.modifyFilm(film);

        topChart = filmServiceTest.getTopChart(10);
        assertEquals(0, topChart.size(), "Updated film has no likes. It shouldn't be in topChart");
    }

    @Test
    void shouldCheckTheCorrectnessOfRating() {
        Integer filmId = film.getId();
        Integer otherFilmId = otherFilm.getId();
        Integer userId = user.getId();
        Integer otherUserId = otherUser.getId();

        filmServiceTest.likeFilm(filmId, userId);
        filmServiceTest.likeFilm(otherFilmId, userId);

        List<Film> topChart = filmServiceTest.getTopChart(10);

        assertTrue(topChart.contains(otherFilm) && topChart.contains(film) && topChart.size() == 2,
                "Should be 2 films in topChart after adding 1 like per film");

        filmServiceTest.likeFilm(filmId, otherUserId);
        topChart = filmServiceTest.getTopChart(10);

        assertTrue(topChart.getFirst().equals(film) && topChart.getLast().equals(otherFilm)
                        && topChart.size() == 2,
                "The 'film' (2 likes) should be the first in topChart and 'otherFilm' (1 like) should be " +
                        "the second with size of topChart = 2");

        filmServiceTest.likeFilm(otherFilmId, otherUserId);
        filmServiceTest.disLikeFilm(filmId, otherUserId);
        topChart = filmServiceTest.getTopChart(10);

        assertTrue(topChart.getFirst().equals(otherFilm) && topChart.getLast().equals(film)
                        && topChart.size() == 2,
                "The 'otherFilm' (2 likes) should be the first in topChart and 'film' (1 like) should be " +
                        "the second with size of topChart = 2");

        filmServiceTest.removeFilm(otherFilmId); //Этот кейс
        topChart = filmServiceTest.getTopChart(10);

        assertTrue(topChart.getFirst().equals(film) && topChart.size() == 1,
                "In topChart should be only 'film' after deletion of 'otherFilm'");
    }

    @Test
    void shouldNotMatterIfLikedByOneUserForSeveralTimes() {
        Integer filmId = film.getId();
        Integer userId = user.getId();

        filmServiceTest.likeFilm(filmId, userId);
        filmServiceTest.likeFilm(filmId, userId);

        List<Film> topChart = filmServiceTest.getTopChart(10);

        assertTrue(topChart.contains(film) && topChart.size() == 1,
                "Incorrect work of program if several likes for the film by one user");
    }

    @Test
    void shouldCheckSizeOfReturningTopChart() {
        Integer filmId = film.getId();
        Integer otherFilmId = otherFilm.getId();
        Integer userId = user.getId();
        Integer otherUserId = otherUser.getId();

        filmServiceTest.likeFilm(filmId, userId);
        filmServiceTest.likeFilm(filmId, otherUserId);
        filmServiceTest.likeFilm(otherFilmId, userId);

        List<Film> topChart = filmServiceTest.getTopChart(10); // default case

        assertTrue(topChart.contains(otherFilm) && topChart.contains(film) && topChart.size() == 2,
                "Should be 2 films in chart");

        topChart = filmServiceTest.getTopChart(0);
        assertTrue(topChart.isEmpty(), "Should be empty as counter is 0");

        topChart = filmServiceTest.getTopChart(1);
        assertTrue(topChart.contains(film) && topChart.size() == 1, "Should contain only one film");
    }
}
