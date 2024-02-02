package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DirectorDbStorageImplTest {
    private final DirectorStorage directorStorage;

    @Test
    public void findAllTest() {
        assertTrue(directorStorage.findAll().isEmpty());
        directorStorage.post(new Director(1, "Ivanov"));
        assertEquals(1, directorStorage.findAll().size());
        directorStorage.post(new Director(2, "Petrov"));
        assertEquals(2, directorStorage.findAll().size());
    }

    @Test
    public void findDirectorByIdTest() {
        assertThrows(EntityNotFoundException.class, () -> directorStorage.findDirectorById(9999));
        directorStorage.post(new Director(1, "Ivanov"));
        directorStorage.post(new Director(2, "Petrov"));
        assertEquals("Petrov", directorStorage.findDirectorById(2).getName());
    }

    @Test
    public void putTest() {
        directorStorage.post(new Director(1, "Ivanov"));
        directorStorage.put(new Director(1, "Petrov"));
        assertEquals("Petrov", directorStorage.findDirectorById(1).getName());
    }

    @Test
    public void delDirectorByIdTest() {
        directorStorage.post(new Director(1, "Ivanov"));
        directorStorage.delDirectorById(1);
        assertTrue(directorStorage.findAll().isEmpty());
    }
}
