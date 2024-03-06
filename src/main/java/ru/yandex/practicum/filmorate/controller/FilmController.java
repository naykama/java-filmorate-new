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
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findFimById(@PathVariable int id) {
        return filmService.findFimById(id);
    }

    @PostMapping()
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
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
    public List<Film> popular(@RequestParam(defaultValue = "10") @Positive(message = "Число не может быть отрицательным") int count,
                              @RequestParam(value = "genreId", required = false) Integer genreId,
                              @RequestParam(value = "year", required = false) @Min(value = 1895, message = "Год должен быть больше 1895") Integer year) {
        List<Film> filmList;
        if (genreId == null && year == null) {
            filmList = filmService.getPopularFilms(count);
        } else {
            return filmService.getMostLikedFilmsByGenreAndYear(count, genreId, year);
        }
        return filmList;
    }

    @GetMapping("/popular/marks")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") @Positive(message = "Число не может быть отрицательным") int count,
                              @RequestParam(value = "genreId", required = false) Integer genreId,
                              @RequestParam(value = "year", required = false) @Min(value = 1895, message = "Год должен быть больше 1895") Integer year) {
        List<Film> filmList;
        if (genreId == null && year == null) {
            filmList = filmService.getPopularFilmsByMarks(count);
        } else {
            filmList = genreId == null ? filmService.getPopularFilmsForYearByMarks(year, count)
                    : year == null ? filmService.getPopularFilmsForGenreByMarks(genreId, count)
                    : filmService.getPopularFilmsForGenreAndYearByMarks(year, genreId, count);
        }
        return filmList;
    }

    @DeleteMapping("/{id}")
    public Film delete(@PathVariable Integer id) {
        return filmService.delete(id);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/director/{id}")
    public List<Film> getFilmsForDirectorSorted(@PathVariable("id") int directorId, @RequestParam SortType sortBy) {
        switch (sortBy) {
            case likes:
                return filmService.getFilmsForDirectorSortedByLikes(directorId);
            case year:
                return filmService.getFilmsForDirectorSortedByYear(directorId);
            case mark:
                return filmService.getRecommendedByMarksFilms(directorId);
            default:
                log.error("Ошибка в параметрах запроса. Переданный параметр = \"{}\"", sortBy);
                throw new IllegalRequestParameterException("Некорректный параметр запроса");
        }
    }

    @GetMapping("/search")
    public List<Film> search(@RequestParam String query, @RequestParam String by) {
        return filmService.search(query, by);
    }

    @PutMapping(value = "/{id}/mark/{userId}")
    public void addMark(@PathVariable int id, @PathVariable int userId, @RequestParam int mark) {
        filmService.addMark(id, userId, mark);
    }

    private enum SortType {
        likes,
        year,
        mark
    }

    @ExceptionHandler
    public ResponseEntity<String> catchValidationException(ConstraintViolationException ex) {
        log.error("Возникла ошибка валидации входного значения");
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
