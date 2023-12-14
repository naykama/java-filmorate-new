package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exeption.UserFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final List<Film> films = new ArrayList<>();
    private int id = 1;

    @Override
    public List<Film> findAll() {
        log.info("Список фильмов выведен, сейчас их количество: " + films.size());
        return films;
    }

    @Override
    public Film post(Film film) {
        film.setId(incrementId());
        films.add(film);
        log.info("Фильм добавлен: " + film.getName());
        return film;
    }

    @Override
    public Film findFimById(int id) {
        Film filmFound = films.stream().filter(film -> film.getId() == id).findFirst().orElseThrow(() -> new UserFoundException("Нет фильма с ID: " + id));
        log.info("Найден фильм под ID: " + films.size());
        return filmFound;
    }

    @Override
    public Film put(Film film) {
        boolean filmIdExist = films.stream().allMatch(userFoeEach -> userFoeEach.getId() == film.getId());
        if (!filmIdExist) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с указанным ID не найден");
        }
        films.removeIf(filmForEach -> filmForEach.getId() == film.getId());
        films.add(film);
        log.info(film.getId() + " обновлен");
        return film;
    }

    private int incrementId() {
        return id++;
    }
}
