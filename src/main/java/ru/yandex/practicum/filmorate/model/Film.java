package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.util.annotation.DateValidation;

import java.time.LocalDate;

@Data
public class Film {
    @NotNull(groups = NotNull.class)
    private Integer id;
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
    @Positive
    private final int duration;
}
