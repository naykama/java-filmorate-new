package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> findAll();

    Film post(Film film);

    Film findFimById(int id);

    Film put(Film film);

    List<Film> popular(int count);

    void addLike(int id, int userId);

    void dellLike(int id, int userId);

    List<Film> commonFilms(int userId, int friendId);

    List<Film> search(String query, String by);
}
