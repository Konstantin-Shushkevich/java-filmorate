package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.repository.SimpleRepository;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Slf4j
public class RatingMpaController {
    private final SimpleRepository<RatingMpa> ratingMpaRepository;

    @GetMapping
    public Collection<RatingMpa> getAll() {
        log.trace("Getting list of all mpa ratings has been started");
        return ratingMpaRepository.getAll();
    }

    @GetMapping("/{id}")
    public RatingMpa getMpaRating(@PathVariable Integer id) {
        log.trace("Searching for mpa rating in progress");
        return ratingMpaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("MPA rating wasn't found"));
    }
}
