package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dao.FriendsUserDB;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public class FilmDbStorageImpl implements FriendsUserDB {

    @Override
    public void addFriends(int id, int friendsId) {

    }

    @Override
    public void dellFriends(Integer id, Integer friendsId) {

    }

    @Override
    public List<User> getFriends(Integer id) {
        return null;
    }

    @Override
    public List<User> getCommonFriends(Integer id, Integer otherId) {
        return null;
    }
//    private RowMapper<User> filmRowMapper() {
//
//    }
}
