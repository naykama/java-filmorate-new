package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorStorage {
    List<Director> findAll();

    Director findDirectorById(int id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void delDirectorById(int id);

    void load(List<Film> films);
}
