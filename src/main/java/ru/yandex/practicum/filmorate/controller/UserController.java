package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@RestController
public class UserController {

    private int id = 1;
    private static final List<User> users = new ArrayList<>();

    @GetMapping("/users")
    public List<User> findAll() {
        log.info("Список пользователей выведен, сейчас их количество: " + users.size());
        return users;
    }

    @PostMapping(value = "/users")
    public User post(@Valid @RequestBody User user) {
        validateUser(user);
        user.setId(incrementId());
        users.add(user);
        log.info(user.getName() + " был добавлен к списку пользователей");
        return user;
    }

    @PutMapping(value = "/users")
    public User put(@Valid @RequestBody User user) {
        validateUser(user);
        boolean userIdExist = users.stream().allMatch(userFoeEach -> userFoeEach.getId() == user.getId());
        if(!userIdExist){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Пользователь с указанным ID не найден");
        }
        users.removeIf(userFoeEach -> userFoeEach.getId() == user.getId());
        users.add(user);
        log.info("\"" + user.getId() + "\" пользователь под данным id был обновлен");
        return user;
    }

    private int incrementId() {
        return id++;
    }

    private void validateUser(User user){
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        if (user.getName() == null) {
            log.info("Поскольку имя отсутствовало, оно было записано так же как и логин");
            user.setName(user.getLogin());
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

}
