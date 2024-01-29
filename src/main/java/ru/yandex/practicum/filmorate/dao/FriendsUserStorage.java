package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

public interface FriendsUserStorage {
    void addFriends(int id, int friendsId);

    void dellFriends(Integer id, Integer friendsId);

    List<User> getFriends(Integer id);

    List<User> getCommonFriends(Integer id, Integer otherId);
}
