package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UserValidationTest {
    private Validator validator;

    @BeforeEach
    void starter() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void userValidTest() {
        User user = new User(1, "mail@mail.ru", "userValidTest_test", "userValidTest name", LocalDate.of(1900, 01, 01));
        Set<ConstraintViolation<User>> validations = validator.validate(user);
        assertTrue(validations.isEmpty());
    }

    @Test
    void userBadEmailValidTest() {
        User user = new User(1, "", "userValidTest_test", "userValidTest name", LocalDate.of(1900, 01, 01));
        Set<ConstraintViolation<User>> validations = validator.validate(user);
        assertTrue(validations.size() == 1);

        User user1 = new User(1, "mail!!mail.ru", "userValidTest_test", "userValidTest name", LocalDate.of(1900, 01, 01));
        Set<ConstraintViolation<User>> validations1 = validator.validate(user1);
        assertTrue(validations.size() == 1);
    }

    @Test
    void userBadLoginValidTest() {
        User user = new User(1, "mail@mail.ru", "userValidTest test", "userValidTest name", LocalDate.of(1900, 01, 01));
        Set<ConstraintViolation<User>> validations = validator.validate(user);
        assertTrue(validations.size() == 1);

        User user1 = new User(1, "mail@mail.ru", " ", "userValidTest name", LocalDate.of(1900, 01, 01));
        Set<ConstraintViolation<User>> validations1 = validator.validate(user1);
        assertTrue(validations.size() == 1);
    }

    @Test
    void userBadBirthdayValidTest() {
        User user = new User(1, "mail@mail.ru", "userValidTest_test", "userValidTest name", LocalDate.of(2900, 01, 01));
        Set<ConstraintViolation<User>> validations = validator.validate(user);
        assertTrue(validations.size() == 1);
    }

    @Test
    void userEmptyNameValidTest() {
        User user = new User(1, "mail@mail.ru", "userValidTest_test", "", LocalDate.of(1900, 01, 01));
        Set<ConstraintViolation<User>> validations = validator.validate(user);
        assertTrue(validations.isEmpty());
    }
}