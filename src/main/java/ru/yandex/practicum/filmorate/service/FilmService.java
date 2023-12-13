package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.FilmFoundException;
import ru.yandex.practicum.filmorate.exeption.FilmLikeException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

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
        Film filmFound = inMemoryFilmStorage.findAll().stream().filter(film -> film.getId() == idFilm).findFirst()
                .orElseThrow(() -> new FilmFoundException("Нет фильма с ID:  + id"));
        if (!user.getFilmsLike().contains(filmFound)) {
            user.getFilmsLike().add(filmFound);
            filmFound.setLike(filmFound.getLike() + 1);
        } else {
            throw new FilmFoundException("Ползовтаель может опавить только один лайк, одному фильму");
        }
    }
    public void dellLike(int idFilm, int idUser) {
        User user = inMemoryUserStorage.findUserById(idUser);
        Film filmFound = inMemoryFilmStorage.findAll().stream().filter(film -> film.getId() == idFilm).findFirst()
                .orElseThrow(() -> new FilmFoundException("Нет фильма с ID:  + id"));
        if (user.getFilmsLike().contains(filmFound)) {
            user.getFilmsLike().remove(filmFound);
            filmFound.setLike(filmFound.getLike() - 1);
        } else {
            throw new FilmLikeException("Пользоталь не тсавил лайк этому фильму");
        }
    }
}
