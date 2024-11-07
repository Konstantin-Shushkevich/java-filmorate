package ru.yandex.practicum.filmorate.controllerTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.LocalDateTimeTypeAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for checking the correctness of validation in UserController.
 */
public class UserControllerTest {
    private static ConfigurableApplicationContext context;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateTimeTypeAdapter())
            .create();

    @BeforeAll
    static void initializeUserController() {
        context = SpringApplication.run(FilmorateApplication.class);
    }

    @AfterAll
    static void afterAll() {
        context.stop();
    }

    @Test
    void shouldPassValidation() throws IOException, InterruptedException {
        User user = new User("test@test.com", "tester", "TesterName", LocalDate.of(2000, 1,
                1));

        String userJson = gson.toJson(user);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/users");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        User userFromRequest = gson.fromJson(response.body(), User.class);
        user.setId(userFromRequest.getId());
        user.setName(userFromRequest.getName());

        assertEquals(200, response.statusCode(),
                "The response code received is different from the expected one. Correct User element wasn't " +
                        "added");
        assertEquals(user, userFromRequest);

        User userNoName = new User("test@test.com", "tester", "", LocalDate.of(2000, 1,
                1));

        userJson = gson.toJson(userNoName);
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        userFromRequest = gson.fromJson(response.body(), User.class);

        assertEquals(200, response.statusCode(),
                "The response code received is different from the expected one");
        assertEquals(userFromRequest.getName(), userFromRequest.getLogin(), "If name is empty, login should " +
                "be used as name");
    }

    @Test
    void shouldFailValidationCauseOfIncorrectEmail() throws IOException, InterruptedException {
        User user = new User("testtest.com", "tester", "TesterName", LocalDate.of(2000, 1,
                1));

        String userJson = gson.toJson(user);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/users");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(),
                "The response code received is different from the expected one. User with incorrect email " +
                        "(no @) was added");

        User user1 = new User("", "tester", "TesterName", LocalDate.of(2000, 1,
                1));

        userJson = gson.toJson(user1);
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(),
                "The response code received is different from the expected one. User with empty email");
    }

    @Test
    void shouldFailValidationCauseOfIncorrectLogin() throws IOException, InterruptedException {
        User user = new User("test@test.com", "", "TesterName", LocalDate.of(2000, 1,
                1));

        String userJson = gson.toJson(user);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/users");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(),
                "The response code received is different from the expected one. Incorrect work with empty " +
                        "login");
    }

    @Test
    void shouldFailValidationCauseOfBirthdayDateInFuture() throws IOException, InterruptedException {
        User user = new User("test@test.com", "tester", "TesterName", LocalDate.of(2030, 1,
                1));

        String userJson = gson.toJson(user);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/users");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(),
                "The response code received is different from the expected one. Incorrect work Birthday " +
                        "date in future ");
    }
}
