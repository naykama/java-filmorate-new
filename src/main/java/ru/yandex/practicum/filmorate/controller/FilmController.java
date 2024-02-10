package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.IllegalRequestParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Min;
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
    public List<Film> popular(@RequestParam(defaultValue = "10") @Positive(message = "Число не может быть отрицательным") int count,
                              @RequestParam(value = "genreId", required = false) Integer genreId,
                              @RequestParam(value = "year", required = false) @Min(value = 1895, message = "Год должен быть больше 1895") Integer year) {
        List<Film> filmList;
        if (genreId == null && year == null) {
            filmList = filmService.getPopularFilms(count);
            log.info("Выведен список популярных фильмов");
        } else {
            log.info("Получен GET запрос на получение самых популярных фильмов по жанру и году");
            return filmService.getMostLikedFilmsByGenreAndYear(count, genreId, year);
        }
        return filmList;
    }

    @DeleteMapping("/{id}")
    public Film delete(@PathVariable Integer id) {
        log.info("Получен DELETE-запрос к эндпоинту: '/films' на удаление фильма с ID={}", id);
        return filmService.delete(id);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        List<Film> filmList = filmService.getСommonFilms(userId, friendId);
        log.info("Выведен список совместных фильмов пользователей под id \"{}\" и \"{}\", размер списка: \"{}\"", userId, friendId, filmList.size());
        return filmList;
    }

    @GetMapping("/director/{id}")
    public List<Film> getFilmsForDirectorSorted(@PathVariable("id") int directorId, @RequestParam SortType sortBy) {
        switch (sortBy) {
            case likes:
                log.info("Выведен список фильмов режиссёра с id = \"{}\", отсортированный по количеству лайков",
                        directorId);
                return filmService.getFilmsForDirectorSortedByLikes(directorId);
            case year:
                log.info("Выведен список фильмов режиссёра с id = \"{}\", отсортированный по году выпуска", directorId);
                return filmService.getFilmsForDirectorSortedByYear(directorId);
            default:
                log.error("Ошибка в параметрах запроса. Переданный параметр = \"{}\"", sortBy);
                throw new IllegalRequestParameterException("Некорректный параметр запроса");
        }
    }

    @GetMapping("/search")
    public List<Film> search(@RequestParam String query, @RequestParam String by) {
        List<Film> filmList = filmService.search(query, by);
        log.info("Выведен список фильмов согласно поиску, по запросу \"{}\"", query);
        return filmList;
    }

    private enum SortType {
        likes,
        year
    }

    @ExceptionHandler
    public ResponseEntity<String> catchValidationException (ConstraintViolationException ex) {
        log.error("Возникла ошибка валидации входного значения");
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
