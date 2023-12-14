package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class FilmController {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(InMemoryFilmStorage inMemoryFilmStorage, FilmService filmService) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.filmService = filmService;
    }
    @GetMapping("/films")
    public List<Film> findAll() {
//        log.info("Список фильмов выведен, сейчас их количество: " + films.size());
        return inMemoryFilmStorage.findAll();
    }

    @GetMapping("/films/{id}")
    public Film findFimById(@PathVariable int id) {
//        log.info("Список фильмов выведен, сейчас их количество: " + films.size());
        return inMemoryFilmStorage.findFimById(id);
    }

    @PostMapping(value = "/films")
    public Film post(@Valid @RequestBody Film film) {
        log.info(film.getName() + " был добавлен к списку филюмов");
        return inMemoryFilmStorage.post(film);
    }

    @PutMapping(value = "/films")
    public Film put(@Valid @RequestBody Film film) {
        log.info("\"" + film.getId() + "\" фильм под данным id был обновлен");
        return inMemoryFilmStorage.put(film);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public void dellLike(@PathVariable int id, @PathVariable int userId) {
        filmService.dellLike(id, userId);
    }
    @GetMapping("/films/popular")
    public List<Film> popular(@RequestParam Optional<Integer> count){
       return filmService.popular(count);
    }

}
