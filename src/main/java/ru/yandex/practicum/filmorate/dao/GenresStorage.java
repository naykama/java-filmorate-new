package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenresStorage {
    List<Genre> genresFindAll();

    Genre genresFindForId(int id);

    void load(List<Film> films);
}
