package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserServiceTest {
    InMemoryUserStorage inMemoryUserStorageTest;
    UserService userServiceTest;
    User user;
    User otherUser;

    @BeforeEach
    public void initialiseTestSpace() {
        inMemoryUserStorageTest = new InMemoryUserStorage();
        userServiceTest = new UserService(inMemoryUserStorageTest);

        user = new User("user@test.com", "user", LocalDate.of(1990, 1, 1));
        otherUser = new User("otherUser@test.com", "otherUser",
                LocalDate.of(2000, 1, 1));

        inMemoryUserStorageTest.saveUser(user);
        inMemoryUserStorageTest.saveUser(otherUser);
    }

    @Test
    public void shouldCheckAddAndDeleteFriend() {
        Integer userId = user.getId();
        Integer otherUserId = otherUser.getId();
        userServiceTest.addFriend(userId, otherUserId);

        assertTrue(user.getFriends().contains(otherUserId) && user.getFriends().size() == 1,
                "The user should have only 1 friend - otherUser");
        assertTrue(otherUser.getFriends().contains(userId) && otherUser.getFriends().size() == 1,
                "The otherUser should have only 1 friend - user");

        try {
            userServiceTest.addFriend(userId, userId);
        } catch (ValidationException ignored) {
        }

        assertTrue(user.getFriends().contains(otherUserId) && user.getFriends().size() == 1,
                "The user can't add own account as friend!");

        userServiceTest.deleteFriend(userId, otherUserId);

        assertTrue(user.getFriends().isEmpty(), "The user must not have any friends after being unfriended");
        assertTrue(otherUser.getFriends().isEmpty(),
                "The otherUser must not have any friends after being unfriended");
    }

    @Test
    void shouldDeleteFromFriendListIfUserIsDeleted() {
        Integer userId = user.getId();
        Integer otherUserId = otherUser.getId();
        userServiceTest.addFriend(userId, otherUserId);
        userServiceTest.deleteUserCompletely(otherUserId);

        assertTrue(user.getFriends().isEmpty(), "If friend's account was deleted, " +
                "it shouldn't be in friend list");
    }

    @Test
    void shouldCheckGettingFriendList() {
        Integer userId = user.getId();
        Integer otherUserId = otherUser.getId();
        userServiceTest.addFriend(userId, otherUserId);

        assertEquals(userServiceTest.getFriendList(userId).getFirst(), otherUser);
        assertEquals(1, userServiceTest.getFriendList(userId).size());
        assertEquals(userServiceTest.getFriendList(otherUserId).getFirst(), user);
        assertEquals(1, userServiceTest.getFriendList(otherUserId).size());
    }

    @Test
    void shouldCheckGettingOfCommonFriendList() {
        User userCommonFriend = new User("friend@test.com", "userCommonFriend",
                LocalDate.of(1980, 1, 1));
        inMemoryUserStorageTest.saveUser(userCommonFriend);

        Integer userId = user.getId();
        Integer otherUserId = otherUser.getId();
        Integer userCommonFriendId = userCommonFriend.getId();
        userServiceTest.addFriend(userId, userCommonFriendId);
        userServiceTest.addFriend(otherUserId, userCommonFriendId);

        assertEquals(user.getFriends(), otherUser.getFriends(), "Common friends lists should be the same");
        assertEquals(1, user.getFriends().size(), "Should be only one common friend");
    }
}
