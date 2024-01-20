package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenresDbStorage {
    List<Genre> genresFindAll();

    Genre genresFindForId(int id);
}
