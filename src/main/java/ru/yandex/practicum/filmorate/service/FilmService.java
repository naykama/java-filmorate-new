package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.GenresStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenresStorage genresStorage;
    private final UserStorage userStorage;

    public List<Film> findAll() {
        List<Film> filmList = filmStorage.findAll();
        genresStorage.load(filmList);
        return filmList;
    }

    public Film post(Film film) {
        return filmStorage.post(film);
    }

    public Film findFimById(int id) {
        List<Film> filmList = List.of(filmStorage.findFimById(id));
        genresStorage.load(filmList);
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

    public List<Film> getMostLikedFilmsByGenreAndYear (int count, int genreId, int year){
        List<Film> filmList = filmStorage.getMostLikedFilmsByGenreAndYear(count, genreId, year);
        genresStorage.load(filmList);
        genresStorage.load(filmList);

        return filmList;
    }

    public void addLike(int id, int userId) {
        User userLike = userStorage.findUserById(userId);
        filmStorage.addLike(id, userId);
    }

    public void dellLike(int id, int userId) {
        User userLike = userStorage.findUserById(userId);
        filmStorage.dellLike(id, userId);
    }

    public Film delete(Integer filmId){
        return filmStorage.delete(filmId);
    }
}
