package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User postUser(User user) {
        user.setId(getNextId());
        setName(user);
        users.put(user.getId(), user);
        log.debug("User {} was successfully added. User id in database is: {}", user.getName(), user.getId());
        return user;
    }

    @Override
    public User putUser(User user) {
        if (users.containsKey(user.getId())) {
            setName(user);
            users.put(user.getId(), user);
            log.debug("User {} was successfully updated", user.getName());
            return user;
        } else {
            log.warn("User {} was not updated: hadn't been added before", user.getName());
            throw new NotFoundException("Trying to update user, that hadn't been added before");
        }
    }

    @Override
    public User deleteUser(Integer id) {
        if (users.containsKey(id)) {
            User user = users.remove(id);
            log.debug("User: {} was deleted", user.getName());
            return user;
        } else {
            log.warn("User with id: {} was not deleted as he/she hadn't been added before", id);
            throw new ValidationException("Trying to delete film, that hadn't been added before");
        }
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public Optional<User> findById(Integer id) {
        return users.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(id))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    private int getNextId() {
        return users.keySet()
                .stream()
                .max(Integer::compare)
                .orElse(0) + 1;
    }

    private void setName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
