package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film post(Film film) {
        return filmStorage.post(film);
    }

    public Film findFimById(int id) {
        return filmStorage.findFimById(id);
    }

    public Film put(Film film) {
        return filmStorage.put(film);
    }

    public List<Film> popular(int count) {
        return filmStorage.popular(count);
    }

    public void addLike(int id, int userId) {
        filmStorage.addLike(id, userId);
    }

    public void dellLike(int id, int userId) {
        filmStorage.dellLike(id, userId);
    }
}
