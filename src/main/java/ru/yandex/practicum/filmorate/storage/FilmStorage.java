package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {
    List<Film> findAll();

    Film post(Film film);

    Film findFimById(Optional<Integer> id);

    Film put(Film film);

    Map<Integer, Film> getFilms();

}
