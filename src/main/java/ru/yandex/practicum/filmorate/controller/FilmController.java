package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping()
    public List<Film> findAll() {
        return filmService.getInMemoryFilmStorage().findAll();
    }

    @GetMapping("/{id}")
    public Film findFimById(@PathVariable int id) {
        return filmService.getInMemoryFilmStorage().findFimById(id);
    }

    @PostMapping()
    public Film post(@Valid @RequestBody Film film) {
        return filmService.getInMemoryFilmStorage().post(film);
    }

    @PutMapping()
    public Film put(@Valid @RequestBody Film film) {
        return filmService.getInMemoryFilmStorage().put(film);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void dellLike(@PathVariable int id, @PathVariable int userId) {
        filmService.dellLike(id, userId);
    }

    @Validated
    @Positive
    @GetMapping("/popular")
    public List<Film> popular(@RequestParam Optional<Integer> count) {
        return filmService.popular(count);
    }

}
