package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@RestController
public class FilmController {
    private final int MAXDESCRIPTIONSIZE = 200;
    private final LocalDate MINRELEASEDATE = LocalDate.of(1895, 12, 28);
    private int id = 1;
    private static final List<Film> films = new ArrayList<>();

    @GetMapping("/films")
    public List<Film> findAll() {
        return films;
    }

    @PostMapping(value = "/films")
    public Film post(@RequestBody Film film) {
        validate(film);
        film.setId(incrementId());
        films.add(film);
        return film;
    }

    @PutMapping(value = "/films")
    public Film put(@RequestBody Film film) {
        validate(film);
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

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > MAXDESCRIPTIONSIZE) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(MINRELEASEDATE)) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}
