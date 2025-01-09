package ru.yandex.practicum.filmorate.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
public class UserExtractor implements ResultSetExtractor<User> {

    @Override
    public User extractData(ResultSet rs) throws SQLException, DataAccessException {
        User user = null;
        Set<Integer> friends = new HashSet<>();

        while (rs.next()) {
            if (user == null) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setLogin(rs.getString("login"));
                user.setName(rs.getString("name"));
                user.setBirthday(LocalDate.parse(rs.getString("birthday")));
            }

            if (rs.getInt("friend_id") != user.getId()) {
                friends.add(rs.getInt("friend_id"));
            }

            if (rs.getInt("user_id") != user.getId() && rs.getBoolean("status")) {
                friends.add(rs.getInt("user_id"));
            }
        }

        if (user == null) {
            return null;
        }

        if (!friends.contains(0)) {
            user.setFriends(friends);
        }

        return user;
    }
}
