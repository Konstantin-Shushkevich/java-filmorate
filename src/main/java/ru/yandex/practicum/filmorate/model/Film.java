package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.util.annotation.DateValidation;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class Film {
    @NotNull(groups = NotNull.class)
    private Integer id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @Size(max = 200)
    private String description;
    @NotNull
    @DateValidation
    @JsonFormat
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private Set<Integer> likes = new HashSet<>();
    private Set<Integer> genres;
    private Integer ratingMPA;

    public void addLike(Integer userId) {
        likes.add(userId);
    }

    public void delLike(Integer userId) {
        likes.remove(userId);
    }
}
