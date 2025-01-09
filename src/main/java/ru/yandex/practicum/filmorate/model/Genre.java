package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@EqualsAndHashCode(of = "id")
public class Genre {
    private int id;

    @Size(max = 50)
    private String name;
}
