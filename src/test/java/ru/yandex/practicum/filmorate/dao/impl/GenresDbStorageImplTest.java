package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.GenresDbStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GenresDbStorageImplTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    void genresFindAll() {
        GenresDbStorage genresDbStorage = new GenresDbStorageImpl(jdbcTemplate);
        List<Genre> genreList = genresDbStorage.genresFindAll();
        assertTrue(genreList.size() == 6);
        assertEquals(genreList.get(0).getId(), 1);
        assertEquals(genreList.get(1).getId(), 2);
        assertEquals(genreList.get(2).getId(), 3);
        assertEquals(genreList.get(3).getId(), 4);
        assertEquals(genreList.get(4).getId(), 5);
        assertEquals(genreList.get(5).getId(), 6);
        assertEquals(genreList.get(0).getName(), "Комедия");
        assertEquals(genreList.get(1).getName(), "Драма");
        assertEquals(genreList.get(2).getName(), "Мультфильм");
        assertEquals(genreList.get(3).getName(), "Триллер");
        assertEquals(genreList.get(4).getName(), "Документальный");
        assertEquals(genreList.get(5).getName(), "Боевик");
    }

    @Test
    void genresFindForId() {
        GenresDbStorage genresDbStorage = new GenresDbStorageImpl(jdbcTemplate);
        Genre genre = genresDbStorage.genresFindForId(2);
        assertNotNull(genre);
        assertEquals(genre.getId(), 2);
        assertEquals(genre.getName(),"Драма");
    }
}