package ru.yandex.practicum.filmorate.repository.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User saveUser(User user);

    User updateUser(User user);

    User deleteUser(Integer id);

    Collection<User> getAll();

    Optional<User> findById(Integer id);

    List<User> findByIds(List<Integer> usersId);

    void addConfirmedFriend(Integer friendId);

    void addUnConfirmedFriend(Integer userId, Integer friendId);

    void deleteConfirmedFriend(Integer userId, Integer friendId);

    void deleteUnConfirmedFriend(Integer userId, Integer friendId);
}
