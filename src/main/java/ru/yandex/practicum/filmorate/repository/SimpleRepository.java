package ru.yandex.practicum.filmorate.repository;

import java.util.Collection;
import java.util.Optional;

public interface SimpleRepository<T> {
    Collection<T> getAll();

    Optional<T> findById(Integer id);
}
