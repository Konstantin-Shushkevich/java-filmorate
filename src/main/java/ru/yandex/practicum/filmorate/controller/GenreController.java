package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.SimpleRepository;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@Slf4j
public class GenreController {
    private final SimpleRepository<Genre> jdbcGenreRepository;

    @GetMapping
    public Collection<Genre> getAll() {
        log.trace("Getting list of all genres has been started");
        return jdbcGenreRepository.getAll();
    }

    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable Integer id) {
        log.trace("Searching for genre in progress");
        return jdbcGenreRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Genre wasn't found"));
    }
}
