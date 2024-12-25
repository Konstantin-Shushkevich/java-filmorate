package ru.yandex.practicum.filmorate.repository.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.extractor.UserExtractor;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcUserRepository implements UserRepository {
    private final NamedParameterJdbcOperations jdbcUsers;

    @Autowired
    private UserExtractor userExtractor;

    @Override
    public User saveUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("email", user.getEmail());
        mapSqlParameterSource.addValue("login", user.getLogin());
        mapSqlParameterSource.addValue("name", user.getName());
        mapSqlParameterSource.addValue("birthday", user.getBirthday());

        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (:email, :login, :name, :birthday)";
        jdbcUsers.update(sql, mapSqlParameterSource, keyHolder);

        int userId;
        if (keyHolder.getKey() != null) {
            userId = keyHolder.getKey().intValue();
            user.setId(userId);
        }

        log.debug("User {} was successfully added. User id in database is: {}", user.getName(), user.getId());

        return user;
    }

    @Override
    public User updateUser(User user) {
        Integer id = user.getId();
        findById(id).orElseThrow(() -> new NotFoundException("User's id doesn't in database"));

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("id", id);
        mapSqlParameterSource.addValue("email", user.getEmail());
        mapSqlParameterSource.addValue("login", user.getLogin());
        mapSqlParameterSource.addValue("name", user.getName());
        mapSqlParameterSource.addValue("birthday", user.getBirthday());

        String sql = "UPDATE users " +
                "SET email = :email, login = :login, name = :name, birthday = :birthday " +
                "WHERE id = :id";

        jdbcUsers.update(sql, mapSqlParameterSource);
        log.debug("User {} was successfully updated", user.getName());

        return user;
    }

    @Override
    public User deleteUser(Integer id) {
        User user = findById(id).orElseThrow(() -> new NotFoundException("User's id doesn't in database"));
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("id", id);

        String sql = "DELETE FROM users WHERE id = :id";
        jdbcUsers.update(sql, mapSqlParameterSource);
        log.debug("User: {} was deleted", user.getName());

        return user;
    }

    @Override
    public Collection<User> getAll() {
        String sql = "SELECT id FROM users";

        List<Integer> usersId = jdbcUsers.getJdbcOperations().queryForList(sql, Integer.class);
        List<User> users = new LinkedList<>();
        User user;

        for (Integer id : usersId) {
            user = findById(id).orElseThrow(() -> new NotFoundException("User's id doesn't in database"));
            users.add(user);
        }

        return users;
    }

    @Override
    public Optional<User> findById(Integer id) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("id", id);
        String sql = "SELECT u.*, f.* FROM users u " +
                "LEFT JOIN friendship f ON (u.id = f.user_id OR u.id = f.friend_id) WHERE u.id = :id";

        return Optional.ofNullable(jdbcUsers.query(sql, mapSqlParameterSource, userExtractor));
    }

    @Override
    public List<User> findByIds(List<Integer> usersId) {
        return usersId.stream()
                .map(id -> findById(id).orElseThrow(() -> new NotFoundException("User's id doesn't in database")))
                .toList();
    }

    public void addFriend(Integer userId, Integer friendId, boolean status) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        String sql;

        if (status) {
            mapSqlParameterSource.addValue("friend_id", friendId);
            mapSqlParameterSource.addValue("status", true);

            sql = "UPDATE friendship " +
                    "SET status = :status " +
                    "WHERE user_id = :friend_id";
        } else {
            mapSqlParameterSource.addValue("user_id", userId);
            mapSqlParameterSource.addValue("friend_id", friendId);
            mapSqlParameterSource.addValue("status", false);

            sql = "INSERT INTO friendship (user_id, friend_id, status) " +
                    "VALUES (:user_id, :friend_id, :status)";
        }

        jdbcUsers.update(sql, mapSqlParameterSource);
    }

    public void deleteFriend(Integer userId, Integer friendId, boolean status) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("user_id", userId);
        mapSqlParameterSource.addValue("friend_id", friendId);
        String sql;

        if (status) {
            mapSqlParameterSource.addValue("status", false);
            sql = "UPDATE friendship SET status = :status " +
                    "WHERE (user_id = :user_id AND friend_id = :friend_id) OR " +
                    "(user_id = :friend_id AND friend_id = :user_id)";
        } else {
            sql = "DELETE FROM friendship " +
                    "WHERE (user_id = :user_id AND friend_id = :friend_id) OR " +
                    "(user_id = :friend_id AND friend_id = :user_id)";
        }
        jdbcUsers.update(sql, mapSqlParameterSource);
    }
}
