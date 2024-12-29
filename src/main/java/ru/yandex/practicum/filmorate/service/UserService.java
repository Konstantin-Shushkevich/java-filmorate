package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.user.JdbcUserRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final JdbcUserRepository jdbcUserRepository;

    public User addFriend(Integer id, Integer friendId) {
        User user = jdbcUserRepository.findById(id).orElseThrow(() ->
                new NotFoundException("User's id doesn't in database"));
        User friend = jdbcUserRepository.findById(friendId).orElseThrow(() ->
                new NotFoundException("Friend's id doesn't in database"));
        log.trace("The user and the friend being added are in the database. Starting of adding...");

        if (user.equals(friend)) {
            throw new ValidationException("You are trying to add yourself as own friend");
        }

        user.addFriend(friendId);
        boolean isFriend = friend.getFriends().contains(user.getId());

        if (isFriend) {
            jdbcUserRepository.addConfirmedFriend(friendId);
        } else {
            jdbcUserRepository.addUnConfirmedFriend(id, friendId);
        }

        log.debug("Friend was successfully added");
        return friend;
    }

    public User deleteFriend(Integer id, Integer friendId) {
        User user = jdbcUserRepository.findById(id).orElseThrow(() ->
                new NotFoundException("User's id doesn't in database"));
        User friend = jdbcUserRepository.findById(friendId).orElseThrow(() ->
                new NotFoundException("Friend's id doesn't in database"));
        log.trace("The user and the friend being added are in the database. Starting of deletion...");

        user.delFriend(friendId);
        boolean isFriend = friend.getFriends().contains(id);

        if (isFriend) {
            jdbcUserRepository.deleteConfirmedFriend(id, friendId);
        } else {
            jdbcUserRepository.deleteUnConfirmedFriend(id, friendId);
        }

        log.debug("Friend was successfully deleted");
        return friend;
    }

    public List<User> getFriendList(Integer userId) {
        User user = jdbcUserRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User's id doesn't in database"));
        log.trace("Requested user is in database");

        List<Integer> friendsId = user.getFriends().stream().toList();

        if (friendsId.isEmpty()) {
            return List.of();
        }

        return jdbcUserRepository.findByIds(friendsId);
    }

    public List<User> getCommonFriendList(Integer id, Integer otherId) {
        User user = jdbcUserRepository.findById(id).orElseThrow(() ->
                new NotFoundException("User's id doesn't in database"));
        User otherUser = jdbcUserRepository.findById(otherId).orElseThrow(() ->
                new NotFoundException("Other user's id doesn't in database"));
        log.trace("User and the other user validation had been passed successfully");

        List<Integer> commonFriendsId = user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .toList();

        if (commonFriendsId.isEmpty()) {
            return List.of();
        }

        return jdbcUserRepository.findByIds(commonFriendsId);
    }

    public User deleteUserCompletely(Integer id) {
        User user = jdbcUserRepository.findById(id).orElseThrow(() ->
                new NotFoundException("User's id you want to delete doesn't in database"));

        List<Integer> friends = new ArrayList<>(user.getFriends());

        if (friends.isEmpty()) {
            return user;
        }

        friends.forEach(friendId -> jdbcUserRepository.findById(friendId).ifPresent(friend -> friend.delFriend(id)));
        friends.forEach(friendId -> jdbcUserRepository.deleteUnConfirmedFriend(friendId, id));

        return jdbcUserRepository.deleteUser(id);
    }

    public void setName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("The name was set to the same as the login");
        }
    }
}
