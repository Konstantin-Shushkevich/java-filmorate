package ru.yandex.practicum.filmorate.util.constant;

public class FilmRepositoryConstants {
    public static final String INSERT_FILM_TO_FILMS =
            "INSERT INTO films (name, description, release_date, " +
                    "duration, mpa_rating_id) " +
                    "VALUES (:name, :description, :release_date, :duration, :mpa_rating_id)";

    public static final String INSERT_VALUES_TO_FILM_GENRE =
            "INSERT INTO film_genre (film_id, genre_id) " +
                    "VALUES (:film_id, :genre_id)";

    public static final String INSERT_FILM_TO_FILMS_IF_UPDATE =
            "INSERT INTO films (id, name, description, " +
                    "release_date, duration, mpa_rating_id) " +
                    "VALUES (:id, :name, :description, :release_date, :duration, :mpa_rating_id)";

    public static final String DELETE_FILM_FROM_FILMS = "DELETE FROM films WHERE id = :id";

    public static final String GET_ALL_ID_FROM_FILMS = "SELECT id FROM films";

    public static final String GET_VALUES_FOR_FILM_MAPPING =
            "SELECT f.*, fg.genre_id, g.genre_name, mr.point_name, l.user_id " +
                    "FROM films f LEFT JOIN film_genre fg ON f.id = fg.film_id " +
                    "LEFT JOIN genre g ON fg.genre_id = g.id " +
                    "LEFT JOIN mpa_rating mr ON mr.id = f.mpa_rating_id " +
                    "LEFT JOIN likes l ON l.film_id = f.id " +
                    "WHERE f.id = :id";

    public static final String INSERT_LIKE_VALUES_TO_LIKES =
            "INSERT INTO likes (user_id, film_id) " +
                    "VALUES (:user_id, :film_id)";

    public static final String DELETE_LIKE_FROM_LIKES =
            "DELETE FROM likes " +
                    "WHERE (user_id = :user_id AND film_id =:film_id)";

    public static final String GET_ALL_ID_FROM_MPA_RATING = "SELECT id FROM mpa_rating";

    public static final String GET_ALL_ID_FROM_GENRE = "SELECT id FROM genre";
}
