package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Event.*;

import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenresStorage genresStorage;
    private final UserStorage userStorage;
    private final DirectorStorage directorStorage;
    private final EventStorage eventStorage;

    public List<Film> findAll() {
        List<Film> filmList = filmStorage.findAll();
        genresStorage.load(filmList);
        directorStorage.load(filmList);
        return filmList;
    }

    public Film post(Film film) {
        return filmStorage.post(film);
    }

    public Film findFimById(int id) {
        List<Film> filmList = List.of(filmStorage.findFimById(id));
        genresStorage.load(filmList);
        directorStorage.load(filmList);
        return filmList.get(0);
    }

    public Film put(Film film) {
        return filmStorage.put(film);
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> filmList = filmStorage.getPopularFilms(count);
        genresStorage.load(filmList);
        return filmList;
    }

    public List<Film> getMostLikedFilmsByGenreAndYear(int count, Integer genreId, Integer year) {
        List<Film> filmList;
        if (genreId == null) {
            genreId = 0;
            filmList = popular(count, genreId, year);
        } else if (year == null) {
            year = 0;
            filmList = popular(count, genreId, year);
        } else {
            filmList = popular(count, genreId, year);
        }
        return filmList;
    }

    public List<Film> popular(int count, int genreId, int year) {
        List<Film> filmList = filmStorage.getMostLikedFilmsByGenreAndYear(count, genreId, year);
        genresStorage.load(filmList);
        directorStorage.load(filmList);
        return  filmList;
    }

    public void addLike(int id, int userId) {
        User userLike = userStorage.findUserById(userId);
        filmStorage.addLike(id, userId);
        eventStorage.createEvent(new Event(userId, id, EventType.LIKE, OperationType.ADD));
    }

    public void dellLike(int id, int userId) {
        User userLike = userStorage.findUserById(userId);
        filmStorage.dellLike(id, userId);
        eventStorage.createEvent(new Event(userId, id, EventType.LIKE, OperationType.REMOVE));
    }


    public List<Film> getFilmsForDirectorSortedByLikes(int directorId) {
        directorStorage.findDirectorById(directorId);
        List<Film> filmList = filmStorage.getFilmsForDirectorSortedByLikes(directorId);
        genresStorage.load(filmList);
        directorStorage.load(filmList);
        return filmList;
    }

    public List<Film> getFilmsForDirectorSortedByYear(int directorId) {
        directorStorage.findDirectorById(directorId);
        List<Film> filmList = filmStorage.getFilmsForDirectorSortedByYear(directorId);
        genresStorage.load(filmList);
        directorStorage.load(filmList);
        return filmList;
    }

    public List<Film> getСommonFilms(int userId, int friendId) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        return filmStorage.getСommonFilms(userId, friendId);
    }

    public List<Film> search(String query, String by) {
        List<Film> filmList = filmStorage.search(query, by);
        genresStorage.load(filmList);
        directorStorage.load(filmList);
        return filmList;
    }

    public Film delete(Integer filmId) {
        return filmStorage.delete(filmId);
    }

    public List<Film> getRecommendedFilms(Integer userId) {
        List<Film> filmList = new ArrayList<>(filmStorage.getRecommendedFilms(userId));
        genresStorage.load(filmList);
        return filmList;
    }
}
