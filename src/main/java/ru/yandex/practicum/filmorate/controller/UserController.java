package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

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
        return inMemoryUserStorage.findAll();
    }

    @GetMapping("/users/{id}")
    public User findUserById(@PathVariable int id) {
        log.info("Список пользователей выведен, сейчас их количество: " + users.size());
        return inMemoryUserStorage.findUserById(id);
    }

    @PostMapping(value = "/users")
    public User post(@Valid @RequestBody User user) {
        log.info(user.getName() + " был добавлен к списку пользователей");
        return inMemoryUserStorage.post(user);
    }

    @PutMapping(value = "/users")
    public User put(@Valid @RequestBody User user) {
        log.info("\"" + user.getId() + "\" пользователь под данным id был обновлен");
        return inMemoryUserStorage.put(user);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public void addFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.addFriends(id, friendId);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public void dellFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.dellFriends(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public ArrayList<User> getFriends(@PathVariable Integer id) {
        return userService.getFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public ArrayList<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    private int incrementId() {
        return id++;
    }
}
