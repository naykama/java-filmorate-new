package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface FilmStorage {
    List<Film> findAll();

    Film post(Film film);

    Film findFimById(int id);

    Film put(Film film);

    List<Film> getPopularFilms(int count);

    List<Film> getMostLikedFilmsByGenreAndYear(int count, int genreId, int year);

    void addLike(int id, int userId);

    void dellLike(int id, int userId);

    Film delete(Integer filmId);

    List<Film> getСommonFilms(int userId, int friendId);

    List<Film> search(String query, String by);

    Set<Film> getRecommendedFilms(Integer userId);

    List<Film> getFilmsForDirectorSortedByLikes(int directorId);

    List<Film> getFilmsForDirectorSortedByYear(int directorId);

    void addMark(int id, int userId, int mark);

    Set<Film> getRecommendedByMarksFilms(Integer userId);

    List<Film> getPopularFilmsByMarks(int count);

    List<Film> getPopularFilmsForGenreByMarks(int genreId, int count);

    List<Film> getPopularFilmsForYearByMarks(int year, int count);

    List<Film> getPopularFilmsForGenreAndYearByMarks(int year, int genreId, int count);

    List<Film> getFilmsForDirectorSortedByMark(int directorId);
}
