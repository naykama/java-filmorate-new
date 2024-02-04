package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorageImpl;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorageImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RecommendationServiceTest {
    private final RecommendationService recommendationService;
    private final JdbcTemplate jdbcTemplate;

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
        filmStorage.addLike(film1.getId(), user2.getId());
        filmStorage.addLike(film2.getId(), user1.getId());
        filmStorage.addLike(film2.getId(), user2.getId());
        filmStorage.addLike(film3.getId(), user2.getId());
        Set<Film> compareList = new HashSet<>();
        compareList.add(film3);
        Set<Film> recommendationListFilms = recommendationService.getRecommendedFilms(user1.getId());
        assertEquals(1, recommendationListFilms.size());
        //assertEquals(compareList, recommendationListFilms);
    }
}
