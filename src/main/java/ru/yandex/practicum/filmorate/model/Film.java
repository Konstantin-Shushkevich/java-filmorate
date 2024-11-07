package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.util.annotation.DateValidation;

import java.time.LocalDate;

@Data
public class Film {
    private int id = -1;
    @NotNull
    @NotBlank
    private final String name;
    @NotNull
    @Size(max = 200)
    private final String description;
    @NotNull
    @DateValidation
    @JsonFormat
    private final LocalDate releaseDate;
    @NotNull
    @Positive
    private final int duration;
}
