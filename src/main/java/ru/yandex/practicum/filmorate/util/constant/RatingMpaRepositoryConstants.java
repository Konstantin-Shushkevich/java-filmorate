package ru.yandex.practicum.filmorate.util.constant;

public class RatingMpaRepositoryConstants {
    public static final String FIND_RATE_BY_ID = "SELECT * FROM mpa_rating WHERE id = :id";
    public static final String GET_ALL_RATES_ID = "SELECT id FROM mpa_rating";
}
