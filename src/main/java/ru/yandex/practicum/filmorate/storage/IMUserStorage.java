package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface IMUserStorage {
    List<User> findAll();

    User findUserById(int id);

    User post(User user);

    User put(User user);
}
