package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        user.setId(setNextId());
        setName(user);
        users.put(user.getId(), user);
        log.debug("User {} was successfully added. User id in database is: {}", user.getName(), user.getId());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        setName(user);

        if (user.getId() == null) {
            log.error("Missing id");
            throw new ValidationException("Trying to update user with missing id");
        } else if (users.containsKey(user.getId())) {
            setName(user);
            users.put(user.getId(), user);
            log.debug("User {} was successfully updated", user.getName());
            return user;
        } else {
            log.debug("User {} was not updated: wasn't added before", user.getName());
            throw new ValidationException("Trying to update user, that hadn't been added before");
        }
    }

    private int setNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void setName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
