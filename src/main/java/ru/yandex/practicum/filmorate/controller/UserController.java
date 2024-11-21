package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserStorage inMemoryUserStorage;
    private final UserService userService;

    @GetMapping
    public Collection<User> getAll() {
        log.trace("Getting list of all users has been started");
        return inMemoryUserStorage.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        log.trace("Adding user is started");
        return inMemoryUserStorage.postUser(user);
    }

    @PutMapping
    @Validated(NotNull.class)
    public User update(@Valid @RequestBody User user) {
        log.trace("Updating user in progress");
        return inMemoryUserStorage.putUser(user);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Integer id) {
        log.trace("Searching for user in progress");
        return inMemoryUserStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("User wasn't found"));
    }

    @DeleteMapping("/{id}")
    public User deleteUser(@PathVariable Integer id) {
        log.trace("Searching for user to delete in progress");
        return inMemoryUserStorage.deleteUser(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.trace("Adding a friend to user's friends list has been started");
        return userService.addFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendList(@PathVariable Integer id) {
        log.trace("Getting list of friends has been started");
        return userService.getFriendList(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriendsList(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.trace("Searching for common friends has been started");
        return userService.getCommonFriendList(id, otherId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.trace("Deletion from friends list has been started");
        return userService.deleteFriend(id, friendId);
    }
}
