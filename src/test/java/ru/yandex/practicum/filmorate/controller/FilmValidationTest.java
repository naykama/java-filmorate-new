package ru.yandex.practicum.filmorate.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmValidationTest {
    private Validator validator;

    @BeforeEach
    void starter() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void filmValidTest() {
        Film film = new Film(2, "filmValidTest name", "filmValidTest film", LocalDate.of(2012, 12, 12), 160);
        Set<ConstraintViolation<Film>> validations = validator.validate(film);
        assertTrue(validations.isEmpty());
    }

    @Test
    void filmBadNameValidTest() {
        Film film = new Film(2, "", "filmBadNameValidTest film", LocalDate.of(2012, 12, 12), 160);
        Set<ConstraintViolation<Film>> validations = validator.validate(film);
        assertTrue(validations.size() == 1);
    }

    @Test
    void filmBadDescriptionValidTest() {
        String description = "description".repeat(201);
        Film film = new Film(2, "filmBadDescriptionValidTest", description, LocalDate.of(2012, 12, 12), 160);
        Set<ConstraintViolation<Film>> validations = validator.validate(film);
        assertTrue(validations.size() == 1);
    }

    @Test
    void filmBadReleaseDateValidTest() {
        Film film = new Film(2, "filmBadDescriptionValidTest", "filmBadReleaseDateValidTest description", LocalDate.of(1850, 12, 12), 160);
        Set<ConstraintViolation<Film>> validations = validator.validate(film);
        assertTrue(validations.size() == 1);
    }

    @Test
    void filmBadDurationValidTest() {
        Film film = new Film(2, "filmBadDescriptionValidTest", "filmBadReleaseDateValidTest description", LocalDate.of(2012, 12, 12), -160);
        Set<ConstraintViolation<Film>> validations = validator.validate(film);
        assertTrue(validations.size() == 1);
    }
}