package ru.yandex.practicum.filmorate.storage;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage{
    private int id = 1;
    private static final List<User> users = new ArrayList<>();

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public User post(User user) {
        user.setId(incrementId());
        users.add(user);
        return user;
    }

    @Override
    public User put(User user) {
        boolean userIdExist = users.stream().allMatch(userFoeEach -> userFoeEach.getId() == user.getId());
        if (!userIdExist) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с указанным ID не найден");
        }
        users.removeIf(userFoeEach -> userFoeEach.getId() == user.getId());
        users.add(user);
        return user;
    }
    private int incrementId() {
        return id++;
    }

}
