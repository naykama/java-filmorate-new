package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDbStorage {

    List<User> findAll();

    User findUserById(int id);

    User post(User user);

    User put(User user);
}
