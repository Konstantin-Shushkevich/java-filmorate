package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage inMemoryUserStorage;

    public User addFriend(Integer id, Integer friendId) {
        User user = inMemoryUserStorage.findById(id).orElseThrow(() ->
                new NotFoundException("User's id doesn't in database"));
        User friend = inMemoryUserStorage.findById(friendId).orElseThrow(() ->
                new NotFoundException("Friend's id doesn't in database"));
        log.trace("The user and the friend being added are in the database. Starting of adding...");

        user.getFriends().add(friendId);
        friend.getFriends().add(id);
        log.debug("Friend was successfully added");
        return friend;
    }

    public User deleteFriend(Integer id, Integer friendId) {
        User user = inMemoryUserStorage.findById(id).orElseThrow(() ->
                new NotFoundException("User's id doesn't in database"));
        User friend = inMemoryUserStorage.findById(friendId).orElseThrow(() ->
                new NotFoundException("Friend's id doesn't in database"));
        log.trace("The user and the friend being added are in the database. Starting of deletion...");

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
        log.debug("Friend was successfully deleted");
        return friend;
    }

    public List<User> getFriendList(Integer userId) {
        User user = inMemoryUserStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("User's id doesn't in database"));
        log.trace("Requested user is in database");

        return user.getFriends()
                .stream()
                .map(inMemoryUserStorage::findById)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriendList(Integer id, Integer otherId) {
        User user = inMemoryUserStorage.findById(id).orElseThrow(() ->
                new NotFoundException("User's id doesn't in database"));
        User otherUser = inMemoryUserStorage.findById(otherId).orElseThrow(() ->
                new NotFoundException("Other user's id doesn't in database"));
        log.trace("User and the other user validation had been passed successfully");

        List<Integer> commonFriendsId = user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .toList();

        if (commonFriendsId.isEmpty()) {
            return List.of();
        }

        return commonFriendsId.stream()
                .map(inMemoryUserStorage::findById)
                .map(Optional::get)
                .toList();
    }
}
