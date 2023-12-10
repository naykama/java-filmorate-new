package ru.yandex.practicum.filmorate.storage;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final List<Film> films = new ArrayList<>();
    private int id = 1;


    @Override
    public List<Film> findAll() {
        return films;
    }

    @Override
    public Film post(Film film) {
        film.setId(incrementId());
        films.add(film);
        return film;
    }

    @Override
    public Film put(Film film) {
        boolean filmIdExist = films.stream().allMatch(userFoeEach -> userFoeEach.getId() == film.getId());
        if (!filmIdExist) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с указанным ID не найден");
        }
        films.removeIf(filmForEach -> filmForEach.getId() == film.getId());
        films.add(film);
        return film;
    }

    private int incrementId() {
        return id++;
    }
}
