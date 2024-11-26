package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User saveUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.debug("User {} was successfully added. User id in database is: {}", user.getName(), user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
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
        User user = users.remove(id);
        log.debug("User: {} was deleted", user.getName());

        return user;
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findByIds(List<Integer> usersId) {
        return usersId
                .stream()
                .map(this::findById)
                .map(Optional::get)
                .toList();
    }

    private int getNextId() {
        return users.keySet()
                .stream()
                .max(Integer::compare)
                .orElse(0) + 1;
    }
}
