package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> findAll();

    User findUserById(int id);

    User post(User user);

    User put(User user);

    void addFriends(Integer id, Integer friendId);

    void dellFriends(Integer id, Integer friendId);

    List<User> getFriends(Integer id);

    List<User> getCommonFriends(Integer id, Integer otherId);
}
