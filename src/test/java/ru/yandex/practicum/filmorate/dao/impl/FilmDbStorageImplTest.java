package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmDbStorageImplTest {
    private final FilmStorage filmStorage;
    private final GenresStorage genresStorage;
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;
    private final DirectorStorage directorStorage;
    private final EventStorage eventStorage;


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
    public void testGetPopularFilmsWithLikes() {
        FilmStorage filmDbStorage = new FilmDbStorageImpl(jdbcTemplate);
        UserStorage userDbStorage = new UserDbStorageImpl(jdbcTemplate);

        Film film1 = new Film(1, "Film 1", "Description 1", LocalDate.of(2022, 1, 1), 120);
        Film film2 = new Film(2, "Film 2", "Description 2", LocalDate.of(2022, 2, 1), 90);
        Film film3 = new Film(3, "Film 3", "Description 3", LocalDate.of(2022, 3, 1), 150);

        film1.setMpa(new Mpa(1, "PG-13"));
        film2.setMpa(new Mpa(2, "R"));
        film3.setMpa(new Mpa(3, "PG"));

        User user1 = new User(1, "user1@example.com", "user1", "User 1", LocalDate.of(1990, 1, 1));
        User user2 = new User(2, "user2@example.com", "user2", "User 2", LocalDate.of(1990, 2, 1));
        User user3 = new User(3, "user3@example.com", "user3", "User 3", LocalDate.of(1990, 3, 1));

        filmDbStorage.post(film1);
        filmDbStorage.post(film2);
        filmDbStorage.post(film3);

        userDbStorage.post(user1);
        userDbStorage.post(user2);
        userDbStorage.post(user3);

        filmDbStorage.addLike(1, 1);
        filmDbStorage.addLike(1, 2);
        filmDbStorage.addLike(2, 1);
        filmDbStorage.addLike(3, 3);

        List<Film> popularFilms = filmDbStorage.getPopularFilms(10);

        String films1 = popularFilms.get(0).getName();
        String films2 = popularFilms.get(1).getName();
        String films3 = popularFilms.get(2).getName();

        Assertions.assertEquals("Film 1", films1);
        Assertions.assertEquals("Film 2", films2);
        Assertions.assertEquals("Film 3", films3);
    }

    @Test
    void addLikeTest() {
        Film filmOne = new Film(1, "filmOne", "testDescription", LocalDate.of(2000, 12, 20), 167);
        Mpa mpaOne = new Mpa(1, "G");
        filmOne.setMpa(mpaOne);
        FilmService filmService = new FilmService(filmStorage,genresStorage,userStorage, directorStorage, eventStorage);
        filmService.post(filmOne);
        User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        UserStorage userStorage = new UserDbStorageImpl(jdbcTemplate);
        userStorage.post(newUser);
        Film filmBeforeLike = filmService.findFimById(newUser.getId());
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
        FilmService filmService = new FilmService(filmStorage,genresStorage,userStorage, directorStorage, eventStorage);
        filmService.post(filmOne);
        User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        UserStorage userStorage = new UserDbStorageImpl(jdbcTemplate);
        userStorage.post(newUser);
        Film filmBeforeLike = filmService.findFimById(filmOne.getId());
        assertTrue(filmBeforeLike.getRate() == 0);
        filmService.dellLike(filmOne.getId(), newUser.getId());
        Film filmAfterLike = filmService.findFimById(filmOne.getId());
        assertTrue(filmAfterLike.getRate() == -1);
    }

    @Test
    public void deleteFilm() {
        Film filmOne = new Film(1, "filmOne", "testDescription", LocalDate.of(2000, 12, 20), 167);
        Mpa mpaOne = new Mpa(1, "G");
        filmOne.setMpa(mpaOne);
        FilmService filmService = new FilmService(filmStorage,genresStorage,userStorage, directorStorage, eventStorage);
        filmService.post(filmOne);
        filmService.delete(1);
        assertThrows(EntityNotFoundException.class, () -> filmService.findFimById(1));
    }

    @Test
    void getFilmsForDirectorSortedByTest() {
        FilmService filmService = new FilmService(filmStorage,genresStorage,userStorage, directorStorage, eventStorage);
        Mpa mpa = new Mpa(1, "G");
        Film filmOne = new Film(1, "filmOne", "testDescription", LocalDate.of(2002, 12, 20), 167);
        Film filmTwo = new Film(2, "filmTwo", "testDescription", LocalDate.of(2001, 12, 20), 167);
        Film filmThree = new Film(3, "filmThree", "testDescription", LocalDate.of(2000, 12, 20), 167);
        Director director = new Director(1, "Ivanov");
        filmOne.setMpa(mpa);
        filmTwo.setMpa(mpa);
        filmThree.setMpa(mpa);
        directorStorage.post(director);
        filmOne.getDirectors().add(director);
        filmTwo.getDirectors().add(director);
        filmService.post(filmOne);
        filmService.post(filmTwo);
        filmService.post(filmThree);
        List<Film> filmsSortedByYear = filmService.getFilmsForDirectorSortedByYear(1);

        assertEquals(2, filmsSortedByYear.size());
        assertEquals("filmTwo", filmsSortedByYear.get(0).getName());
        List<Film> filmsSortedByLikes = filmService.getFilmsForDirectorSortedByLikes(1);
        assertEquals(2, filmsSortedByLikes.size());
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
        List<Film> films = filmDbStorage.getСommonFilms(1, 2);
        assertTrue(films.size() == 1);
        assertEquals(films.get(0).getName(), filmOne.getName());
        assertEquals(films.get(0).getDuration(), filmOne.getDuration());
        filmDbStorage.dellLike(1, 1);
        List<Film> filmsNew = filmDbStorage.getСommonFilms(1, 2);
        assertTrue(filmsNew.isEmpty());
    }

    @Test
    void searchTest() {
        FilmService filmService = new FilmService(filmStorage, genresStorage, userStorage, directorStorage, eventStorage);
        Mpa mpa = new Mpa(1, "G");
        Film filmOne = new Film(1, "One", "testDescription", LocalDate.of(2002, 12, 20), 167);
        Film filmTwo = new Film(2, "film for Ivanov", "testDescription", LocalDate.of(2001, 12, 20), 167);
        Director director = new Director(1, "Ivanov");
        filmOne.setMpa(mpa);
        filmTwo.setMpa(mpa);
        directorStorage.post(director);
        filmOne.getDirectors().add(director);
        filmService.post(filmOne);
        filmService.post(filmTwo);
        List<Film> filmListDirector = filmService.search("iva", "director");
        assertTrue(filmListDirector.contains(filmOne));

        List<Film> filmListTitle = filmService.search("iva", "title");
        assertTrue(filmListTitle.contains(filmTwo));
        User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        UserStorage userStorage = new UserDbStorageImpl(jdbcTemplate);
        userStorage.post(newUser);
        filmStorage.addLike(2, 1);
        List<Film> filmListTitleAndDirector = filmService.search("iva", "title,director");
        System.out.println("filmListTitleAndDirector.size() " + filmListTitleAndDirector.size());
        assertTrue(filmListTitleAndDirector.size() == 2);
        assertEquals(filmListTitleAndDirector.get(0).getName(), filmTwo.getName());
        assertEquals(filmListTitleAndDirector.get(1).getName(), filmOne.getName());
    }

    @Test
    void recommendationFilmList() {
        User user1 = new User(1, "mail1@mail.ru", "Test_user1", "", LocalDate.of(1985, 01, 01));
        User user2 = new User(2, "mail2@mail.ru", "Test_user2", "", LocalDate.of(1975, 02, 02));
        Film film1 = new Film(1, "Test_film1", "Test_film1_description", LocalDate.of(1995, 07, 01), 120);
        Film film2 = new Film(2, "Test_film2", "Test_film2_description", LocalDate.of(2012, 10, 05), 140);
        Film film3 = new Film(3, "Test_film3", "Test_film3_description", LocalDate.of(2018, 12, 11), 160);
        Mpa mpaOne = new Mpa(1, "G");
        Mpa mpaTwo = new Mpa(2, "PG");
        Mpa mpaThree = new Mpa(3,"PG-13");
        film1.setMpa(mpaOne);
        film2.setMpa(mpaTwo);
        film3.setMpa(mpaThree);
        UserStorage userStorage = new UserDbStorageImpl(jdbcTemplate);
        FilmStorage filmStorage = new FilmDbStorageImpl(jdbcTemplate);
        userStorage.post(user1);
        userStorage.post(user2);
        filmStorage.post(film1);
        filmStorage.post(film2);
        filmStorage.post(film3);
        filmStorage.addLike(film1.getId(), user1.getId());
        film1.setRate(film1.getRate() + 1);
        filmStorage.addLike(film1.getId(), user2.getId());
        film1.setRate(film1.getRate() + 1);
        filmStorage.addLike(film2.getId(), user1.getId());
        film2.setRate(film2.getRate() + 1);
        filmStorage.addLike(film2.getId(), user2.getId());
        film2.setRate(film2.getRate() + 1);
        filmStorage.addLike(film3.getId(), user2.getId());
        film3.setRate(film3.getRate() + 1);
        Set<Film> recommendationListFilms = filmStorage.getRecommendedFilms(user1.getId());
        Set<Film> compareListFilms = new HashSet<>();
        compareListFilms.add(film3);
        assertEquals(1, recommendationListFilms.size());
        assertEquals(recommendationListFilms, compareListFilms);
    }

    @Test
    void recommendationFilmListIsEmpty() {
        User user1 = new User(1, "mail1@mail.ru", "Test_user1", "", LocalDate.of(1985, 01, 01));
        User user2 = new User(2, "mail2@mail.ru", "Test_user2", "", LocalDate.of(1975, 02, 02));
        Film film1 = new Film(1, "Test_film1", "Test_film1_description", LocalDate.of(1995, 07, 01), 120);
        Film film2 = new Film(2, "Test_film2", "Test_film2_description", LocalDate.of(2012, 10, 05), 140);
        Film film3 = new Film(3, "Test_film3", "Test_film3_description", LocalDate.of(2018, 12, 11), 160);
        Mpa mpaOne = new Mpa(1, "G");
        Mpa mpaTwo = new Mpa(2, "PG");
        Mpa mpaThree = new Mpa(3,"PG-13");
        film1.setMpa(mpaOne);
        film2.setMpa(mpaTwo);
        film3.setMpa(mpaThree);
        UserStorage userStorage = new UserDbStorageImpl(jdbcTemplate);
        FilmStorage filmStorage = new FilmDbStorageImpl(jdbcTemplate);
        userStorage.post(user1);
        userStorage.post(user2);
        filmStorage.post(film1);
        filmStorage.post(film2);
        filmStorage.post(film3);
        Set<Film> recommendationListFilms = filmStorage.getRecommendedFilms(user1.getId());
        assertEquals(0, recommendationListFilms.size());
    }

    @Test
    void recommendationFilmListEqualCountLikes() {
        User user1 = new User(1, "mail1@mail.ru", "Test_user1", "", LocalDate.of(1985, 01, 01));
        User user2 = new User(2, "mail2@mail.ru", "Test_user2", "", LocalDate.of(1975, 02, 02));
        Film film1 = new Film(1, "Test_film1", "Test_film1_description", LocalDate.of(1995, 07, 01), 120);
        Film film2 = new Film(2, "Test_film2", "Test_film2_description", LocalDate.of(2012, 10, 05), 140);
        Film film3 = new Film(3, "Test_film3", "Test_film3_description", LocalDate.of(2018, 12, 11), 160);
        Mpa mpaOne = new Mpa(1, "G");
        Mpa mpaTwo = new Mpa(2, "PG");
        Mpa mpaThree = new Mpa(3,"PG-13");
        film1.setMpa(mpaOne);
        film2.setMpa(mpaTwo);
        film3.setMpa(mpaThree);
        UserStorage userStorage = new UserDbStorageImpl(jdbcTemplate);
        FilmStorage filmStorage = new FilmDbStorageImpl(jdbcTemplate);
        userStorage.post(user1);
        userStorage.post(user2);
        filmStorage.post(film1);
        filmStorage.post(film2);
        filmStorage.post(film3);
        filmStorage.addLike(film1.getId(), user1.getId());
        film1.setRate(film1.getRate() + 1);
        filmStorage.addLike(film1.getId(), user2.getId());
        film1.setRate(film1.getRate() + 1);
        filmStorage.addLike(film2.getId(), user1.getId());
        film2.setRate(film2.getRate() + 1);
        filmStorage.addLike(film2.getId(), user2.getId());
        film2.setRate(film2.getRate() + 1);
        filmStorage.addLike(film3.getId(), user1.getId());
        film3.setRate(film3.getRate() + 1);
        filmStorage.addLike(film3.getId(), user2.getId());
        film3.setRate(film3.getRate() + 1);
        Set<Film> recommendationListFilms = filmStorage.getRecommendedFilms(user1.getId());
        assertEquals(0, recommendationListFilms.size());
    }
}