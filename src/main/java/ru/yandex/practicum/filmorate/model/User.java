package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class User {
    @NotNull(groups = NotNull.class)
    private Integer id;

    @NotNull
    @NotBlank
    @Size(max = 255)
    @Email
    private String email;

    @NotNull
    @NotBlank
    @Size(max =50)
    @Pattern(regexp = "^\\S*$")
    private String login;

    @Size(max = 100)
    private String name;

    @NotNull
    @JsonFormat
    @PastOrPresent
    private LocalDate birthday;

    private Set<Integer> friends = new HashSet<>();

    public void addFriend(Integer id) {
        friends.add(id);
    }

    public void delFriend(Integer id) {
        friends.remove(id);
    }
}
