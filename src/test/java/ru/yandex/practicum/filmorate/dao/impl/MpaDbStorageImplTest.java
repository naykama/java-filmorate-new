package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MpaDbStorageImplTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    void findMpaForIdTest() {
        MpaStorage mpaDbStorage = new MpaDbStorageImpl(jdbcTemplate);
        Mpa mpaG = mpaDbStorage.findMpaForId(1);
        Mpa mpaPG = mpaDbStorage.findMpaForId(2);
        assertEquals(mpaG.getId(), 1);
        assertEquals(mpaPG.getId(), 2);
        assertEquals(mpaG.getName(), "G");
        assertEquals(mpaPG.getName(), "PG");
    }

    @Test
    void mpaFindAll() {
        MpaStorage mpaDbStorage = new MpaDbStorageImpl(jdbcTemplate);
        List<Mpa> mpaList = mpaDbStorage.mpaFindAll();
        assertTrue(mpaList.size() == 5);
        assertEquals(mpaList.get(0).getId(), 1);
        assertEquals(mpaList.get(1).getId(), 2);
        assertEquals(mpaList.get(2).getId(), 3);
        assertEquals(mpaList.get(3).getId(), 4);
        assertEquals(mpaList.get(4).getId(), 5);
        assertEquals(mpaList.get(0).getName(), "G");
        assertEquals(mpaList.get(1).getName(), "PG");
        assertEquals(mpaList.get(2).getName(), "PG-13");
        assertEquals(mpaList.get(3).getName(), "R");
        assertEquals(mpaList.get(4).getName(), "NC-17");
    }
}