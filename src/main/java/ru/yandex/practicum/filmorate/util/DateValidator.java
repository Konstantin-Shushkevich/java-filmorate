package ru.yandex.practicum.filmorate.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.util.annotation.DateValidation;

import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.util.Constants.START_OF_CINEMA_EPOCH;

public class DateValidator implements ConstraintValidator<DateValidation, LocalDate> {

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate.isAfter(START_OF_CINEMA_EPOCH);
    }
}
