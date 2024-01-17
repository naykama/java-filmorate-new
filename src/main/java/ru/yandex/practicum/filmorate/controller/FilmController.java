package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@Validated
@RequiredArgsConstructor

public class FilmController {
    private final FilmService filmService;
    private final FilmDbStorage filmDbStorage;

    @GetMapping()
    public List<Film> findAll() {
        return filmDbStorage.findAll();
    }

    @GetMapping("/{id}")
    public Film findFimById(@PathVariable int id) {
        return filmService.findFimById(id);
    }

    @PostMapping()
    public Film post(@Valid @RequestBody Film film) {
        return filmDbStorage.post(film);
    }

    @PutMapping()
    public Film put(@Valid @RequestBody Film film) {
        return filmService.put(film);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void dellLike(@PathVariable int id, @PathVariable int userId) {
        filmService.dellLike(id, userId);
    }


    @GetMapping("/popular")
    public List<Film> popular(@RequestParam(defaultValue = "10") @Positive int count) {
        return filmService.popular(count);
    }

}
