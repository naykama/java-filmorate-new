package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping()
    public List<Film> findAll() {
        List<Film> filmList = filmService.findAll();
        log.info("Список фильмов выведен, их количество \"{}\"", filmList.size());
        return filmList;
    }

    @GetMapping("/{id}")
    public Film findFimById(@PathVariable int id) {
        Film film = filmService.findFimById(id);
        log.info("Фильм под номером \"{}\" выведен", film.getId());
        return film;
    }

    @PostMapping()
    public Film post(@Valid @RequestBody Film film) {
        Film filmPost = filmService.post(film);
        log.info("Фильм под номером \"{}\" добавлен", film.getId());
        return filmPost;
    }

    @PutMapping()
    public Film put(@Valid @RequestBody Film film) {
        Film filmPut = filmService.put(film);
        log.info("Фильм под номером \"{}\" обновлен", film.getId());
        return filmPut;
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
        log.info("Фильму под номером \"{}\", поставил лайк, пользователь под номером \"{}\"", id, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void dellLike(@PathVariable int id, @PathVariable int userId) {
        filmService.dellLike(id, userId);
        log.info("Фильму под номером \"{}\", удалили лайк, пользователь под номером \"{}\"", id, userId);

    }


    @GetMapping("/popular")
    public List<Film> popular(@RequestParam(defaultValue = "10") @Positive int count) {
        List<Film> filmList = filmService.popular(count);
        log.info("Выведен список популярных фильмов");
        return filmList;
    }

    @GetMapping("/common")
    public List<Film> commonFilms(@RequestParam int userId, @RequestParam int friendId) {
        List<Film> filmList = filmService.commonFilms(userId, friendId);
        log.info("Выведен список совместных фильмов пользователей под id \"{}\" и \"{}\", размер списка: \"{}\"", userId, friendId, filmList.size());
        return filmList;
    }

}
