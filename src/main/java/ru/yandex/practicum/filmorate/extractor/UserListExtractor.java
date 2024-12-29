package ru.yandex.practicum.filmorate.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
public class UserListExtractor implements ResultSetExtractor<List<User>> {
    @Override
    public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<User> users = new LinkedList<>();
        int currentId = 0;
        User user = null;
        Set<Integer> friends = new HashSet<>();

        while (rs.next()) {
            if (rs.getInt("id") != currentId) {

                if (user != null) {

                    if (!friends.contains(0)) {
                        user.setFriends(friends);
                    }

                    users.add(user);
                }

                friends = new HashSet<>();
                user = new User();

                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setLogin(rs.getString("login"));
                user.setName(rs.getString("name"));
                user.setBirthday(LocalDate.parse(rs.getString("birthday")));
            }

            if (user == null) {
                throw new InternalServerException("Something went wrong. User is null");
            }

            currentId = user.getId();

            if (rs.getInt("friend_id") != user.getId()) {
                friends.add(rs.getInt("friend_id"));
            }

            if (rs.getInt("user_id") != user.getId() && rs.getBoolean("status")) {
                friends.add(rs.getInt("user_id"));
            }
        }

        if (user == null) {
            return users;
        }

        if (!friends.contains(0)) {
            user.setFriends(friends);
        }

        users.add(user);

        return users;
    }
}
