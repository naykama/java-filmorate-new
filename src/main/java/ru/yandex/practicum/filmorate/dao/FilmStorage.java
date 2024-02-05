package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> findAll();

    Film post(Film film);

    Film findFimById(int id);

    Film put(Film film);

    List<Film> getPopularFilms(int count);

    List<Film> getMostLikedFilmsByGenreAndYear (int count, int genreId, int year);

    void addLike(int id, int userId);

    void dellLike(int id, int userId);

    Film delete(Integer filmId);

    List<Film> getСommonFilms(int userId, int friendId);

    List<Film> search(String query, String by);
}
