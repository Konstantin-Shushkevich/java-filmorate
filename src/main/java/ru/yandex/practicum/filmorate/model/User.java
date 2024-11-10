package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    @NotNull(groups = NotNull.class)
    private Integer id;
    @NotNull
    @NotBlank
    @Email
    private final String email;
    @NotNull
    @NotBlank
    @Pattern(regexp = "^\\S*$")
    private final String login;
    private String name;
    @NotNull
    @JsonFormat
    @PastOrPresent
    private final LocalDate birthday;
}
