package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface IMFilmStorage {
    List<Film> findAll();

    Film post(Film film);

    Film findFimById(int id);

    Film put(Film film);
}
