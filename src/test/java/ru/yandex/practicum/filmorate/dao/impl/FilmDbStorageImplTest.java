package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.GenresStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmDbStorageImplTest {
    private final FilmStorage filmStorage;
    private final GenresStorage genresStorage;
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;


    @Test
    void findAllTest() {
        Film filmOne = new Film(1, "filmOne", "testDescription", LocalDate.of(2000, 12, 20), 167);
        Film filmTwo = new Film(2, "filmTwo", "testDescription", LocalDate.of(2000, 12, 20), 167);
        Mpa mpaOne = new Mpa(1, "G");
        Mpa mpaTwo = new Mpa(2, "PG");
        filmOne.setMpa(mpaOne);
        filmTwo.setMpa(mpaTwo);
        FilmStorage filmDbStorage = new FilmDbStorageImpl(jdbcTemplate);
        filmDbStorage.post(filmOne);
        filmDbStorage.post(filmTwo);
        List<Film> filmList = filmDbStorage.findAll();
        assertFalse(filmList.isEmpty());
        assertTrue(filmList.size() == 2);
        assertEquals(filmList.get(0), filmOne);
        assertEquals(filmList.get(1), filmTwo);
    }

    @Test
    void findFimByIdTest() {
        Film filmOne = new Film(1, "filmOne", "testDescription", LocalDate.of(2000, 12, 20), 167);
        Mpa mpaOne = new Mpa(1, "G");
        filmOne.setMpa(mpaOne);
        FilmStorage filmDbStorage = new FilmDbStorageImpl(jdbcTemplate);
        filmDbStorage.post(filmOne);
        Film film = filmDbStorage.findFimById(1);
        assertNotNull(film);
        assertEquals(film, filmOne);
        assertThrows(EntityNotFoundException.class, () -> filmDbStorage.findFimById(9999));
    }

    @Test
    void postTest() {
        Film filmOne = new Film(1, "filmOne", "testDescription", LocalDate.of(2000, 12, 20), 167);
        Mpa mpaOne = new Mpa(1, "G");
        filmOne.setMpa(mpaOne);
        FilmStorage filmDbStorage = new FilmDbStorageImpl(jdbcTemplate);
        filmDbStorage.post(filmOne);
        Film film = filmDbStorage.findFimById(1);
        assertEquals(film, filmOne);
        assertEquals(film.getName(), filmOne.getName());

    }

    @Test
    void putTest() {
        Film filmOne = new Film(1, "filmOne", "testDescription", LocalDate.of(2000, 12, 20), 167);
        Film filmTwo = new Film(1, "filmTwo", "testDescription", LocalDate.of(2000, 12, 20), 167);
        Mpa mpaOne = new Mpa(1, "G");
        Mpa mpaTwo = new Mpa(2, "PG");
        filmOne.setMpa(mpaOne);
        filmTwo.setMpa(mpaTwo);
        FilmStorage filmDbStorage = new FilmDbStorageImpl(jdbcTemplate);
        filmDbStorage.post(filmTwo);
        Film film = filmDbStorage.findFimById(1);
        assertEquals(film, filmTwo);
    }

    @Test
    void popularTest() {
        Film filmOne = new Film(1, "filmOne", "testDescription", LocalDate.of(2000, 12, 20), 167);
        Film filmTwo = new Film(2, "filmTwo", "testDescription", LocalDate.of(2000, 12, 20), 167);
        Film filmThree = new Film(3, "filmThree", "testDescription", LocalDate.of(2000, 12, 20), 167);
        Mpa mpaOne = new Mpa(1, "G");
        Mpa mpaTwo = new Mpa(2, "PG");
        Mpa mpaThree = new Mpa(3, "G");
        FilmService filmService = new FilmService(filmStorage, genresStorage, userStorage);
        filmOne.setMpa(mpaOne);
        filmTwo.setMpa(mpaTwo);
        filmThree.setMpa(mpaThree);
        User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        User newUser2 = new User(2, "user2@email.ru", "petya", "Petia Petrov", LocalDate.of(1990, 1, 1));
        UserStorage userStorage = new UserDbStorageImpl(jdbcTemplate);
        userStorage.post(newUser);
        userStorage.post(newUser2);
        filmService.post(filmOne);
        filmService.post(filmTwo);
        filmService.post(filmThree);
        filmService.addLike(2, 1);
        filmService.addLike(2, 2);
        filmService.addLike(1, 2);
        List<Film> filmList = filmService.popular(10);
        assertTrue(filmList.size() == 3);
        assertEquals(filmList.get(0).getName(), "filmTwo");
        assertEquals(filmList.get(1).getName(), "filmOne");
        assertEquals(filmList.get(2).getName(), "filmThree");
        List<Film> filmListTwo = filmService.popular(2);
        assertTrue(filmListTwo.size() == 2);
        assertEquals(filmListTwo.get(0).getName(), "filmTwo");
        assertEquals(filmListTwo.get(1).getName(), "filmOne");
    }

    @Test
    void addLikeTest() {
        Film filmOne = new Film(1, "filmOne", "testDescription", LocalDate.of(2000, 12, 20), 167);
        Mpa mpaOne = new Mpa(1, "G");
        filmOne.setMpa(mpaOne);
        FilmService filmService = new FilmService(filmStorage, genresStorage, userStorage);
        filmService.post(filmOne);
        User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        UserStorage userStorage = new UserDbStorageImpl(jdbcTemplate);
        userStorage.post(newUser);
        Film filmBeforeLike = filmService.findFimById(1);
        assertTrue(filmBeforeLike.getRate() == 0);
        filmService.addLike(1, 1);
        Film filmAfterLike = filmService.findFimById(1);
        assertTrue(filmAfterLike.getRate() == 1);
    }

    @Test
    void dellLikeTest() {
        Film filmOne = new Film(1, "filmOne", "testDescription", LocalDate.of(2000, 12, 20), 167);
        Mpa mpaOne = new Mpa(1, "G");
        filmOne.setMpa(mpaOne);
        FilmService filmService = new FilmService(filmStorage, genresStorage, userStorage);
        filmService.post(filmOne);
        User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        UserStorage userStorage = new UserDbStorageImpl(jdbcTemplate);
        userStorage.post(newUser);
        Film filmBeforeLike = filmService.findFimById(1);
        assertTrue(filmBeforeLike.getRate() == 0);
        filmService.dellLike(1, 1);
        Film filmAfterLike = filmService.findFimById(1);
        assertTrue(filmAfterLike.getRate() == -1);
    }

    @Test
    void commonFilmsTest() {
        Film filmOne = new Film(1, "filmOne", "testDescription", LocalDate.of(2000, 12, 20), 167);
        Film filmTwo = new Film(2, "filmTwo", "testDescription", LocalDate.of(2000, 12, 20), 167);
        Mpa mpaOne = new Mpa(1, "G");
        Mpa mpaTwo = new Mpa(2, "PG");
        filmOne.setMpa(mpaOne);
        filmTwo.setMpa(mpaTwo);
        FilmStorage filmDbStorage = new FilmDbStorageImpl(jdbcTemplate);
        filmDbStorage.post(filmOne);
        filmDbStorage.post(filmTwo);
        User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        User newUser2 = new User(2, "user2@email.ru", "petya", "Petia Petrov", LocalDate.of(1990, 1, 1));
        UserStorage userStorage = new UserDbStorageImpl(jdbcTemplate);
        userStorage.post(newUser);
        userStorage.post(newUser2);
        filmDbStorage.addLike(1, 1);
        filmDbStorage.addLike(1, 2);
        filmDbStorage.addLike(2, 1);
        List<Film> films = filmDbStorage.commonFilms(1, 2);
        assertTrue(films.size() == 1);
        assertEquals(films.get(0).getName(), filmOne.getName());
        assertEquals(films.get(0).getDuration(), filmOne.getDuration());
        filmDbStorage.dellLike(1, 1);
        List<Film> filmsNew = filmDbStorage.commonFilms(1, 2);
        assertTrue(filmsNew.isEmpty());
    }
}