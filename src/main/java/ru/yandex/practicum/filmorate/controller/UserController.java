package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    private int id = 1;
    private static final Map<Integer, User> users = new HashMap();

    @GetMapping("/users")
    public Map<Integer, User> findAll() {
        return users;
    }

    @PostMapping(value = "/users")
    public User post(@RequestBody User user) {
        if(user.getEmail().isBlank() || !user.getEmail().contains("@")){
            throw new ValidationException("Почтa не может быть пустой и должна содержать домен");
        }else if(user.getLogin().isBlank()){
            throw new ValidationException("Логин не может быть пустым");
        }
        else if(user.getName().isBlank()){

        }
        else if(user.getBirthday().isAfter(LocalDate.now())){
            throw new ValidationException("Дата рождения не может быть в будущем");

        }
        user.setId(incrementId());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping(value = "/users")
    public User put(@RequestBody User user) {
        if (users.containsKey(user.getEmail())) {
            users.remove(user.getEmail());
        }
        users.put(user.getId(), user);
        return user;
    }

    private int incrementId(){
        return id++;
    }

}
