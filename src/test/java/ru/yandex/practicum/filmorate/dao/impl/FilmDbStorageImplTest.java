package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmDbStorageImplTest {
    private final JdbcTemplate jdbcTemplate;
    private final UserService userService;
    private final FriendsDbUserImpl friendsUserDB;


    @Test
    void findAllTest() {
        Film filmOne = new Film(1, "filmOne", "testDescription", LocalDate.of(2000, 12, 20), 167);
        Film filmTwo = new Film(2, "filmTwo", "testDescription", LocalDate.of(2000, 12, 20), 167);
        Mpa mpaOne = new Mpa(1, "G");
        Mpa mpaTwo = new Mpa(2, "PG");
        filmOne.setMpa(mpaOne);
        filmTwo.setMpa(mpaTwo);
        FilmStorage filmDbStorage = new FilmDbStorageImpl(jdbcTemplate, userService);
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
        FilmStorage filmDbStorage = new FilmDbStorageImpl(jdbcTemplate, userService);
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
        FilmStorage filmDbStorage = new FilmDbStorageImpl(jdbcTemplate, userService);
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
        FilmStorage filmDbStorage = new FilmDbStorageImpl(jdbcTemplate, userService);
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
        FilmStorage filmDbStorage = new FilmDbStorageImpl(jdbcTemplate, userService);
        filmOne.setMpa(mpaOne);
        filmTwo.setMpa(mpaTwo);
        filmThree.setMpa(mpaThree);
        User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        User newUser2 = new User(2, "user2@email.ru", "petya", "Petia Petrov", LocalDate.of(1990, 1, 1));
        UserStorage userStorage = new UserDbStorageImpl(jdbcTemplate, friendsUserDB);
        userStorage.post(newUser);
        userStorage.post(newUser2);
        filmDbStorage.post(filmOne);
        filmDbStorage.post(filmTwo);
        filmDbStorage.post(filmThree);
        filmDbStorage.addLike(2, 1);
        filmDbStorage.addLike(2, 2);
        filmDbStorage.addLike(1, 2);
        List<Film> filmList = filmDbStorage.popular(10);
        assertTrue(filmList.size() == 3);
        assertEquals(filmList.get(0).getName(), "filmTwo");
        assertEquals(filmList.get(1).getName(), "filmOne");
        assertEquals(filmList.get(2).getName(), "filmThree");
        List<Film> filmListTwo = filmDbStorage.popular(2);
        assertTrue(filmListTwo.size() == 2);
        assertEquals(filmListTwo.get(0).getName(), "filmTwo");
        assertEquals(filmListTwo.get(1).getName(), "filmOne");
    }

    @Test
    void addLikeTest() {
        Film filmOne = new Film(1, "filmOne", "testDescription", LocalDate.of(2000, 12, 20), 167);
        Mpa mpaOne = new Mpa(1, "G");
        filmOne.setMpa(mpaOne);
        filmOne.setRate(20);
        FilmStorage filmDbStorage = new FilmDbStorageImpl(jdbcTemplate, userService);
        filmDbStorage.post(filmOne);
        User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        UserStorage userStorage = new UserDbStorageImpl(jdbcTemplate, friendsUserDB);
        userStorage.post(newUser);
        Film filmBeforeLike = filmDbStorage.findFimById(1);
        assertTrue(filmBeforeLike.getRate() == 0);
        filmDbStorage.addLike(1, 1);
        Film filmAfterLike = filmDbStorage.findFimById(1);
        assertTrue(filmAfterLike.getRate() == 1);
    }

    @Test
    void dellLikeTest() {
        Film filmOne = new Film(1, "filmOne", "testDescription", LocalDate.of(2000, 12, 20), 167);
        Mpa mpaOne = new Mpa(1, "G");
        filmOne.setMpa(mpaOne);
        FilmStorage filmDbStorage = new FilmDbStorageImpl(jdbcTemplate, userService);
        filmDbStorage.post(filmOne);
        User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        UserStorage userStorage = new UserDbStorageImpl(jdbcTemplate, friendsUserDB);
        userStorage.post(newUser);
        Film filmBeforeLike = filmDbStorage.findFimById(1);
        assertTrue(filmBeforeLike.getRate() == 0);
        filmDbStorage.dellLike(1, 1);
        Film filmAfterLike = filmDbStorage.findFimById(1);
        assertTrue(filmAfterLike.getRate() == -1);
    }
}