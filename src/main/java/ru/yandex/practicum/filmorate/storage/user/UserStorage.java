package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User postUser(User user);

    User putUser(User user);

    User deleteUser(Integer id);

    Collection<User> getAll();

    Optional<User> findById(Integer id);
}
