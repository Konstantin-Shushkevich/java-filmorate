package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

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
        if (users.containsKey(id)) {
            User user = users.remove(id);
            log.debug("User: {} was deleted", user.getName());

            List<Integer> friends = new ArrayList<>(user.getFriends());

            if (friends.isEmpty()) {
                return user;
            }

            friends.stream()
                    .map(friendId -> findById(friendId).get())
                    .peek(user1 -> user1.delFriend(id))
                    .collect(Collectors.toList());

            return user;
        } else {
            log.warn("User with id: {} was not deleted as he/she hadn't been added before", id);
            throw new ValidationException("Trying to delete user, that hadn't been added before");
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

    @Override
    public List<User> findFriends(User user) {
        return user.getFriends()
                .stream()
                .map(this::findById)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findCommonFriends(User user, User otherUser) {
        List<Integer> commonFriendsId = user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .toList();

        if (commonFriendsId.isEmpty()) {
            return List.of();
        }

        return commonFriendsId.stream()
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
