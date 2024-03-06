package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenresService;
import ru.yandex.practicum.filmorate.service.UserService;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MarkTest {
    private final JdbcTemplate jdbcTemplate;
    private final FilmService filmService;
    private final UserService userService;
    private final GenresService genresService;

    @BeforeEach
    private void createContext() {
        List<Genre> genres = genresService.genresFindAll();
        Film film = new Film(1, "film1", "filmDescr",
                LocalDate.of(2001, 10, 23), 30);
        film.setMpa(new Mpa(1, "G"));
        LinkedHashSet<Genre> filmGenres = new LinkedHashSet<>();
        filmGenres.add(genres.get(0));
        film.setGenres(filmGenres);
        filmService.createFilm(film);
        Film film2 = new Film(2, "film2", "filmDescr",
                LocalDate.of(1993, 5, 3), 30);
        film2.setMpa(new Mpa(1, "G"));
        filmService.createFilm(film2);
        Film film3 = new Film(3, "film3", "filmDescr",
                LocalDate.of(1990, 7, 25), 30);
        film3.setMpa(new Mpa(1, "G"));
        film3.setGenres(filmGenres);
        filmService.createFilm(film3);
        Film film4 = new Film(4, "film4", "filmDescr",
                LocalDate.of(2010, 8, 19), 30);
        LinkedHashSet<Genre> film4Genres = new LinkedHashSet<>();
        film4Genres.add(genres.get(3));
        film4.setMpa(new Mpa(1, "G"));
        film4.setGenres(film4Genres);
        filmService.createFilm(film4);

        userService.post(new User(1, "user1@mail.ru", "user", "user", LocalDate.of(2000,
                3,12)));
        userService.post(new User(2, "friend@mail.ru", "friend", "", LocalDate.of(1999,
                4, 16)));
        userService.post(new User(3, "user2@mail.ru", "user2", "user2", LocalDate.of(2001,
                3,12)));

        filmService.addMark(1,1,7);
        filmService.addMark(2,1,4);
        filmService.addMark(1, 2, 5);
        filmService.addMark(2,2,7);
        filmService.addMark(3,2,4);
        filmService.addMark(4, 2, 8);
        filmService.addMark(1,3,4);
        filmService.addMark(3, 3, 5);
    }

    @Test
    public void testGetOneRecommendation() {
        List<Film> films = filmService.getRecommendedByMarksFilms(1);
        assertEquals(1, films.size());
        assertEquals(filmService.findFimById(4), films.get(0));
    }

    @Test
    public void testUserWithoutCommonMarkedFilms() {
        addUserWithoutCommonMarkedFilms();
        List<Film> films = filmService.getRecommendedByMarksFilms(1);
        assertEquals(1, films.size());
        assertEquals(filmService.findFimById(4), films.get(0));
    }

    @Test
    public void testUserWithoutMarks() {
        addUserWithoutMarkedFilms();
        List<Film> films = filmService.getRecommendedByMarksFilms(1);
        assertEquals(1, films.size());
        assertEquals(filmService.findFimById(4), films.get(0));
    }

    @Test
    public void testGetPopular() {
        List<Film> films = filmService.getPopularFilmsByMarks(3);
        assertEquals(3, films.size());
        assertEquals(4, films.get(0).getId());
        assertEquals(1, films.get(2).getId());
        System.out.println(films);
    }

    @Test
    public void testGetPopularFilmsForGenreByMarks() {
        List<Film> films = filmService.getPopularFilmsForGenreByMarks(1, 2);
        assertEquals(2, films.size());
    }

    @Test
    public void testGetPopularFilmsForYearByMarks() {
        List<Film> films = filmService.getPopularFilmsForYearByMarks(1990, 2);
        assertEquals(1, films.size());
        assertEquals(3, films.get(0).getId());
    }

    @Test
    public void testGetPopularFilmsForGenreAndYearByMarks() {
        List<Film> films = filmService.getPopularFilmsForGenreAndYearByMarks(1990, 1, 10);
        assertEquals(1, films.size());
        assertEquals(3, films.get(0).getId());
    }

    private void addUserWithoutCommonMarkedFilms() {
        userService.post(new User(4, "user4@mail.ru", "user4", "user4", LocalDate.of(2001,
                3,12)));
        filmService.addMark(4, 4, 9);
    }

    private void addUserWithoutMarkedFilms() {
        userService.post(new User(4, "user4@mail.ru", "user4", "user4", LocalDate.of(2001,
                3,12)));
    }
}
