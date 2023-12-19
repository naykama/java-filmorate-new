package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    List<User> findAll();

    User findUserById(int id);

    User post(User user);

    User put(User user);
}
