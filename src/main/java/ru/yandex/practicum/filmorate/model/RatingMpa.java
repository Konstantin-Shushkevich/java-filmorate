package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
public class RatingMpa {
    private int id;

    @Size(max = 10)
    private String name;
}
