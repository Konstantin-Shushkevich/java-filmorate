package ru.yandex.practicum.filmorate.util.constant;

public class UserRepositoryConstants {
    public static final String INSERT_USER_TO_USERS =
            "INSERT INTO users (email, login, name, birthday) " +
                    "VALUES (:email, :login, :name, :birthday)";

    public static final String INSERT_USER_TO_USERS_IF_UPDATE =
            "UPDATE users " +
                    "SET email = :email, login = :login, name = :name, birthday = :birthday " +
                    "WHERE id = :id";

    public static final String DELETE_USER_FROM_USERS = "DELETE FROM users WHERE id = :id";

    public static final String GET_ALL_ID_FROM_USERS = "SELECT id FROM users";

    public static final String GET_VALUES_FOR_USER_MAPPING =
            "SELECT u.*, f.* FROM users u " +
                    "LEFT JOIN friendship f ON (u.id = f.user_id OR u.id = f.friend_id) " +
                    "WHERE u.id = :id";

    public static final String UPDATE_FRIENDSHIP_STATUS =
            "UPDATE friendship " +
                    "SET status = :status " +
                    "WHERE user_id = :friend_id";

    public static final String INSERT_NEW_LINE_TO_FRIENDSHIP =
            "INSERT INTO friendship (user_id, friend_id, status) " +
                    "VALUES (:user_id, :friend_id, :status)";

    public static final String UPDATE_FRIENDSHIP_STATUS_IF_DELETE =
            "UPDATE friendship SET status = :status " +
                    "WHERE (user_id = :user_id AND friend_id = :friend_id) OR " +
                    "(user_id = :friend_id AND friend_id = :user_id)";

    public static final String DELETE_FRIENDSHIP_COMPLETELY =
            "DELETE FROM friendship " +
                    "WHERE (user_id = :user_id AND friend_id = :friend_id) OR " +
                    "(user_id = :friend_id AND friend_id = :user_id)";
}
