package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements IMFilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @Override
    public List<Film> findAll() {
        log.info("Список фильмов выведен, сейчас их количество: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film post(Film film) {
        film.setId(incrementId());
        films.put(film.getId(), film);
        log.info("Фильм добавлен: {}", film.getName());
        return film;
    }

    @Override
    public Film findFimById(int id) {
        if (!films.containsKey(id)) {
            throw new EntityNotFoundException("Нет фильма с ID: " + id);
        } else {
            log.info("Найден фильм под ID: {}", films.size());
            return films.get(id);
        }
    }

    @Override
    public Film put(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с указанным ID не найден");
        }
        films.put(film.getId(), film);
        log.info("{} обновлен", film.getId());
        return film;
    }

    private int incrementId() {
        return id++;
    }
}
