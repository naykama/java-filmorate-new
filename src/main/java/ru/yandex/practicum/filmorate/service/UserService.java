package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventStorage;
import ru.yandex.practicum.filmorate.dao.FriendsUserStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Event.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendsUserStorage friendsUserStorage;
    private final EventStorage eventStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findUserById(int id) {
        return userStorage.findUserById(id);
    }

    public User post(User user) {
        checkValidName(user);
        return userStorage.post(user);
    }

    public User put(User user) {
        checkValidName(user);
        return userStorage.put(user);
    }

    public void addFriends(Integer id, Integer friendId) {
        friendsUserStorage.addFriends(id, friendId);
        eventStorage.createEvent(new Event(id, friendId, EventType.FRIEND, OperationType.ADD));
    }

    public void dellFriends(Integer id, Integer friendId) {
        friendsUserStorage.dellFriends(id, friendId);
        eventStorage.createEvent(new Event(id, friendId, EventType.FRIEND, OperationType.REMOVE));
    }

    public List<User> getFriends(Integer id) {
        findUserById(id);
        return friendsUserStorage.getFriends(id);
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        return friendsUserStorage.getCommonFriends(id, otherId);
    }

    public List<Event> getEventsForUserByID(int userId) {
        userStorage.findUserById(userId);
        return eventStorage.getEventsForUserByID(userId);
    }

    private void checkValidName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public User delete(Integer userId){
        return userStorage.delete(userId);
    }
}
