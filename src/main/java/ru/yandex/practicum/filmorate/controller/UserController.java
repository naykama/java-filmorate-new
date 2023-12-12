package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController

public class UserController {
    private final UserService userService;
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserController(UserService userService, InMemoryUserStorage inMemoryUserStorage) {
        this.userService = userService;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }
    private int id = 1;
    private static final List<User> users = new ArrayList<>();

    @GetMapping("/users")
    public List<User> findAll() {
        log.info("Список пользователей выведен, сейчас их количество: " + users.size());
        return inMemoryUserStorage.findAll();
    }
    @GetMapping("/users/{id}")
    public User findUserById(@PathVariable int id) {
        log.info("Список пользователей выведен, сейчас их количество: " + users.size());
        return inMemoryUserStorage.findUserBeId(id);
    }

    @PostMapping(value = "/users")
    public User post(@Valid @RequestBody User user) {
//        validateUser(user);
//        user.setId(incrementId());
//        users.add(user);
        log.info(user.getName() + " был добавлен к списку пользователей");
//        return user;
        return inMemoryUserStorage.post(user);
    }

    @PutMapping(value = "/users")
    public User put(@Valid @RequestBody User user) {
//        validateUser(user);
//        boolean userIdExist = users.stream().allMatch(userFoeEach -> userFoeEach.getId() == user.getId());
//        if (!userIdExist) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с указанным ID не найден");
//        }
//        users.removeIf(userFoeEach -> userFoeEach.getId() == user.getId());
//        users.add(user);

        log.info("\"" + user.getId() + "\" пользователь под данным id был обновлен");
//        return user;
    return inMemoryUserStorage.put(user);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public void addFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.addFriends(id,friendId);
    }
    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public void dellFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.dellFriends(id,friendId);
    }

    @GetMapping("/users/{id}/friends")
    public ArrayList<User> getFriends(@PathVariable Integer id) {
        return userService.getFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public ArrayList<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getCommonFriends(id,otherId);
    }




    private int incrementId() {
        return id++;
    }


//    private void validateUser(User user) {
//        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
//            log.error("Электронная почта не может быть пустой и должна содержать символ @");
//            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
//        }
//
//        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
//            log.error("Логин не может быть пустым и содержать пробелы");
//            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
//        }
//
//        if (user.getName() == null) {
//            log.info("Поскольку имя отсутствовало, оно было записано так же как и логин");
//            user.setName(user.getLogin());
//        }
//
//        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
//            log.error("Дата рождения не может быть в будущем");
//            throw new ValidationException("Дата рождения не может быть в будущем");
//        }
//    }

}
