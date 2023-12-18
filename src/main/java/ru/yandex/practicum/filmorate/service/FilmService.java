package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exeption.FilmLikeException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmPopularComparator;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public void addLike(int idFilm, int idUser) {
        User user = inMemoryUserStorage.findUserById(idUser);
        Film filmFound;
        if (!inMemoryFilmStorage.getFilms().containsKey(idFilm)) {
            throw new EntityNotFoundException("Нет фильма с ID: " + idFilm);
        } else {
            filmFound = inMemoryFilmStorage.getFilms().get(idFilm);
        }
//        Film filmFound = inMemoryFilmStorage.getFilms().stream().filter(film -> film.getId() == idFilm).findFirst().orElseThrow(() -> new EntityNotFoundException("Нет фильма с ID:  + id"));
        if (!user.getFilmsLike().contains(filmFound)) {
            user.getFilmsLike().add(filmFound);
            filmFound.setRate(filmFound.getRate() + 1);
            log.info(String.format("Пользователь: \"{}\", поставил лайк фильму: \"{}\"", user.getLogin(), filmFound.getName()));
        } else {
            throw new EntityNotFoundException("Пользователь может поставить только один лайк, одному фильму");
        }
    }

    public void dellLike(int idFilm, int idUser) {
        User user = inMemoryUserStorage.findUserById(idUser);
//        Film filmFound = inMemoryFilmStorage.getFilms().stream().filter(film -> film.getId() == idFilm).findFirst().orElseThrow(() -> new EntityNotFoundException("Нет фильма с ID:  + id"));
        Film filmFound = null;
        if (!inMemoryFilmStorage.getFilms().containsKey(idFilm)) {
            throw new EntityNotFoundException("Нет фильма с ID: " + idFilm);
        } else {
            filmFound = inMemoryFilmStorage.getFilms().get(idFilm);
        }
        if (user.getFilmsLike().contains(filmFound)) {
            user.getFilmsLike().remove(filmFound);
            filmFound.setRate(filmFound.getRate() - 1);
            log.info(String.format("Пользователь: \"{}\", убрал лайк фильму: \"{}\"", user.getLogin(), filmFound.getName()));
        } else {
            throw new FilmLikeException("Пользователь не ставил лайк этому фильму");
        }
    }

    public List<Film> popular(Optional<Integer> count) {
        List<Film> films = new ArrayList<>(inMemoryFilmStorage.getFilms().values());
        Collections.sort(films, new FilmPopularComparator());
        if (!count.isPresent()) {
            if (films.size() <= 10) {
                log.info("Выведены популярные фильмы");
                return films;
            } else {
                List<Film> firstTenFilms = films.subList(0, 11);
                log.info("Выведены популярные фильмы");
                return firstTenFilms;
            }
        } else {
            List<Film> firstCountFilms = films.subList(0, count.get());
            log.info("Выведены популярные фильмы");
            return firstCountFilms;
        }
    }

    public InMemoryFilmStorage getInMemoryFilmStorage() {
        return inMemoryFilmStorage;
    }

}
