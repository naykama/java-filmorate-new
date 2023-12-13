package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class FilmController {
//    private final int maxDescriptionSize = 200;
//    private final LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(InMemoryFilmStorage inMemoryFilmStorage,FilmService filmService){
        this.inMemoryFilmStorage=inMemoryFilmStorage;
        this.filmService=filmService;
    }

//    private int id = 1;
//    private static final List<Film> films = new ArrayList<>();


    @GetMapping("/films")
    public List<Film> findAll() {
//        log.info("Список фильмов выведен, сейчас их количество: " + films.size());
        return inMemoryFilmStorage.findAll();
    }

    @PostMapping(value = "/films")
    public Film post(@Valid @RequestBody Film film) {
//        validate(film);
//        film.setId(incrementId());
//        films.add(film);
        log.info(film.getName() + " был добавлен к списку филюмов");
        return inMemoryFilmStorage.post(film);
    }

    @PutMapping(value = "/films")
    public Film put(@Valid @RequestBody Film film) {
//        validate(film);
//        boolean filmIdExist = films.stream().allMatch(userFoeEach -> userFoeEach.getId() == film.getId());
//        if (!filmIdExist) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с указанным ID не найден");
//        }
//        films.removeIf(filmForEach -> filmForEach.getId() == film.getId());
//        films.add(film);
        log.info("\"" + film.getId() + "\" фильм под данным id был обновлен");
        return inMemoryFilmStorage.put(film);
    }
    @PutMapping(value = "/films/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id,userId);
    }
    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public void dellLike(@PathVariable int id, @PathVariable int userId) {
        filmService.dellLike(id,userId);
    }

//    private int incrementId() {
//        return id++;
//    }

//    private void validate(Film film) {
//        if (film.getName() == null || film.getName().isBlank()) {
//            log.error("Название не может быть пустым");
//            throw new ValidationException("Название не может быть пустым");
//        }
//        if (film.getDescription().length() > maxDescriptionSize) {
//            log.error("Максимальная длина описания — 200 символов");
//            throw new ValidationException("Максимальная длина описания — 200 символов");
//        }
//        if (film.getReleaseDate().isBefore(minReleaseDate)) {
//            log.error("Дата релиза — не раньше 28 декабря 1895 года");
//            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
//        }
//        if (film.getDuration() < 0) {
//            log.error("Продолжительность фильма должна быть положительной");
//            throw new ValidationException("Продолжительность фильма должна быть положительной");
//        }
//    }
}
