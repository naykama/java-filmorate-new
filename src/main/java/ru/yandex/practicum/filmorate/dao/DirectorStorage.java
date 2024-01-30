package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    List<Director> findAll();

    Director findDirectorById(int id);

    Director post(Director director);

    Director put(Director director);

    void delDirectorById(int id);
}
