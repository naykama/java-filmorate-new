package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDbStorage {
    List<Film> findAll();

    Film post(Film film);

    Film findFimById(int id);

    Film put(Film film);
}
