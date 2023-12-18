package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserStorage {
    List<User> findAll();

    User findUserById(Optional<Integer> id);

    User post(User user);

    User put(User user);

    Map<Integer, User> getUsers();
}
