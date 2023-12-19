package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exeption.FilmLikeException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmPopularComparator;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findFimById(int id) {
        return filmStorage.findFimById(id);
    }

    public Film post(Film film) {
        return filmStorage.post(film);
    }

    public Film put(Film film) {
        return filmStorage.put(film);
    }

    public void addLike(int idFilm, int idUser) {
        User user = userStorage.findUserById(idUser);
        Film filmFound;
        filmFound = filmStorage.findFimById(idFilm);
        if (!user.getFilmsLike().contains(filmFound)) {
            user.getFilmsLike().add(filmFound);
            filmFound.setRate(filmFound.getRate() + 1);
            log.info(String.format("Пользователь: \"{}\", поставил лайк фильму: \"{}\"", user.getLogin(), filmFound.getName()));
        } else {
            throw new EntityNotFoundException("Пользователь может поставить только один лайк, одному фильму");
        }
    }

    public void dellLike(int idFilm, int idUser) {
        User user = userStorage.findUserById(idUser);
        Film filmFound;
        filmFound = filmStorage.findFimById(idFilm);
        if (user.getFilmsLike().contains(filmFound)) {
            user.getFilmsLike().remove(filmFound);
            filmFound.setRate(filmFound.getRate() - 1);
            log.info(String.format("Пользователь: \"{}\", убрал лайк фильму: \"{}\"", user.getLogin(), filmFound.getName()));
        } else {
            throw new FilmLikeException("Пользователь не ставил лайк этому фильму");
        }
    }

    public List<Film> popular(int count) {
        List<Film> films = new ArrayList<>(filmStorage.findAll());
        Collections.sort(films, new FilmPopularComparator());
        if (count == 10) {
            if (films.size() <= 10) {
                log.info("Выведены популярные фильмы");
                return films;
            } else {
                List<Film> firstTenFilms = films.subList(0, 10);
                log.info("Выведены популярные фильмы");
                return firstTenFilms;
            }
        } else {
            List<Film> firstCountFilms = films.subList(0, count);
            log.info("Выведены популярные фильмы");
            return firstCountFilms;
        }
    }
}
