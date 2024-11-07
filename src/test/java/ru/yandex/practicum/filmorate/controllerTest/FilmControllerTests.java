package ru.yandex.practicum.filmorate.controllerTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.LocalDateTimeTypeAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for checking the correctness of validation in FilmController
 */
public class FilmControllerTests {
    private static ConfigurableApplicationContext context;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateTimeTypeAdapter())
            .create();

    @BeforeAll
    static void initializeFilmController() {
        context = SpringApplication.run(FilmorateApplication.class);
    }

    @AfterAll
    static void afterAll() {
        context.stop();
    }

    @Test
    void shouldPassValidation() throws IOException, InterruptedException {
        Film film = new Film("Test 1", "Description", LocalDate.of(2000, 1, 1),
                120);
        String filmJson = gson.toJson(film);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/films");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Film filmFromRequest = gson.fromJson(response.body(), Film.class);
        film.setId(filmFromRequest.getId());

        assertEquals(200, response.statusCode(),
                "The response code received is different from the expected one. Correct Film element wasn't" +
                        "added");
        assertEquals(film, filmFromRequest);

        Film firstFilm = new Film("Workers Leaving the Lumiere Factory", "The first movie",
                LocalDate.of(1895, 12, 28), 1);

        filmJson = gson.toJson(firstFilm);
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),
                "The response code received is different from the expected one. ReleaseDate validation goes" +
                        "wrong");
    }

    @Test
    void shouldFailValidationCauseOfEmptyName() throws IOException, InterruptedException {
        Film film1 = new Film("", "Description", LocalDate.of(2000, 1, 1),
                120);
        String filmJson = gson.toJson(film1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/films");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(),
                "The response code received is different from the expected one");

        Film film2 = new Film(" ", "Description", LocalDate.of(2000, 1, 1),
                120);
        filmJson = gson.toJson(film2);
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(),
                "The response code received is different from the expected one");
    }

    @Test
    void shouldFailValidationCauseOfLongDescription() throws IOException, InterruptedException {
        RandomString randomString = new RandomString(201);
        Film film = new Film("Test 1", randomString.nextString(), LocalDate.of(2000, 1, 1),
                120);

        String filmJson = gson.toJson(film);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/films");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(),
                "The response code received is different from the expected one. Validation shouldn't pass" +
                        "if length of description is more than 200 symbols");
    }

    @Test
    void shouldFailValidationCauseOfDate() throws IOException, InterruptedException {
        Film film = new Film("Test 1", "Description", LocalDate.of(1800, 1, 1),
                120);

        String filmJson = gson.toJson(film);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/films");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(),
                "The response code received is different from the expected one. Current date is earlier than" +
                        " the first movie was made");
    }

    @Test
    void shouldFailValidationCauseOfIncorrectDuration() throws IOException, InterruptedException {
        Film film1 = new Film("Test 1", "Description", LocalDate.of(2000, 1, 1),
                0);

        String filmJson = gson.toJson(film1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/films");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(),
                "The response code received is different from the expected one. Current duration is 0");

        Film film2 = new Film("Test 2", "Description", LocalDate.of(2000, 1, 1),
                -1);

        filmJson = gson.toJson(film2);
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(),
                "The response code received is different from the expected one. Current duration is negative");
    }
}
