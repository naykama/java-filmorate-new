package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserComparator;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findUserById(int id) {
        return userStorage.findUserById(id);
    }

    public User post(User user) {
        return userStorage.post(user);
    }

    public User put(User user) {
        return userStorage.put(user);
    }

    public void addFriends(int id, int friendsId) {
        User userOne = userStorage.findUserById(id);
        User userTwo = userStorage.findUserById(friendsId);
        userOne.getFriends().add(friendsId);
        userTwo.getFriends().add(id);
        log.info("Пользователь \"{}\" добавил \"{}\", в друзья", userOne.getLogin(), userTwo.getLogin());
    }

    public void dellFriends(Integer id, Integer friendsId) {
        User userOne = userStorage.findUserById(id);
        User userTwo = userStorage.findUserById(friendsId);
        userOne.getFriends().remove(friendsId);
        userTwo.getFriends().remove(id);
        log.info(String.format("Пользователь \"{}\" удалил \"{}\", из друзей", userOne.getLogin(), userTwo.getLogin()));

    }

    public List<User> getFriends(Integer id) {
        Set<User> friendsList = new HashSet<>();
        User user = userStorage.findUserById(id);
        for (Integer friend : user.getFriends()) {
            friendsList.add(userStorage.findAll().stream().filter(user1 -> user1.getId() == friend).findFirst().get());
        }
        List<User> arrayUser = new ArrayList<>(friendsList);
        Collections.sort(arrayUser, new UserComparator());
        log.info("Выведены друзья пользователя: {}", user.getLogin());
        return arrayUser;
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        Set<User> friendsList = new HashSet<>();
        User userOne = userStorage.findUserById(id);
        User userTwo = userStorage.findUserById(otherId);
        Set<Integer> otherSet = new HashSet<>(userOne.getFriends());
        otherSet.retainAll(userTwo.getFriends());
        for (Integer friend : otherSet) {
            friendsList.add(userStorage.findAll().stream().filter(user1 -> user1.getId() == friend).findFirst().get());
        }
        List<User> arrayUser = new ArrayList<>(friendsList);
        Collections.sort(arrayUser, new UserComparator());
        log.info("Выведены общине друзья пользователя");
        return arrayUser;
    }
}
