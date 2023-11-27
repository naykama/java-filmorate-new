package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private int id = 1;
    private static final List<User> users = new ArrayList<>();

    @GetMapping("/users")
    public List<User> findAll() {
        return users;
    }

    @PostMapping(value = "/users")
    public User post(@RequestBody User user) {
        validateUser(user);
        user.setId(incrementId());
        users.add(user);
        return user;
    }

    @PutMapping(value = "/users")
    public User put(@RequestBody User user) {
        validateUser(user);
        boolean userIdExist = users.stream().allMatch(userFoeEach -> userFoeEach.getId() == user.getId());
        if(!userIdExist){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Пользователь с указанным ID не найден");
        }
        users.removeIf(userFoeEach -> userFoeEach.getId() == user.getId());
        users.add(user);
        return user;
    }

    private int incrementId() {
        return id++;
    }

    private void validateUser(User user){
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

}
