package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserComparator;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {
    private final InMemoryUserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriends(int id, int friendsId) {
        User userOne = userStorage.findAll().stream().filter(userFinde -> userFinde.getId() == id).findFirst().orElseThrow(() -> new EntityNotFoundException("Нет пользователя с ID: " + id));
        User userTwo = userStorage.findAll().stream().filter(userFinde -> userFinde.getId() == friendsId).findFirst().orElseThrow(() -> new EntityNotFoundException("Нет пользователя с ID: " + friendsId));
        userOne.getFriends().add(friendsId);
        userTwo.getFriends().add(id);
        log.info(String.format("Пользователь \"%s\" добавил \"%s\", в друзья", userOne.getLogin(), userTwo.getLogin()));
    }

    public void dellFriends(Integer id, Integer friendsId) {
        User userOne = userStorage.findAll().stream().filter(userFinde -> userFinde.getId() == id).findFirst().orElseThrow(() -> new EntityNotFoundException("Нет пользователя с ID: " + id));
        User userTwo = userStorage.findAll().stream().filter(userFinde -> userFinde.getId() == friendsId).findFirst().orElseThrow(() -> new EntityNotFoundException("Нет друга с таким id: " + friendsId));
        userOne.getFriends().remove(friendsId);
        userTwo.getFriends().remove(id);
        log.info(String.format("Пользователь \"%s\" удалил \"%s\", из друзей", userOne.getLogin(), userTwo.getLogin()));

    }

    public ArrayList<User> getFriends(Integer id) {
        Set<User> friendsList = new HashSet<>();
        User user = userStorage.findAll().stream().filter(userFinde -> userFinde.getId() == id).findFirst().orElseThrow(() -> new EntityNotFoundException("Нет пользователя с ID: " + id));
        for (Integer friend : user.getFriends()) {
            friendsList.add(userStorage.findAll().stream().filter(user1 -> user1.getId() == friend).findFirst().get());
        }
        ArrayList<User> arrayUser = new ArrayList<>(friendsList);
        Collections.sort(arrayUser, new UserComparator());
        log.info("Выведены друзья пользователя: " + user.getLogin());
        return arrayUser;
    }

    public ArrayList<User> getCommonFriends(Integer id, Integer otherId) {
        Set<User> friendsList = new HashSet<>();
        User userOne = userStorage.findAll().stream().filter(userFinde -> userFinde.getId() == id).findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Нет пользователя с ID: " + id));
        User userTwo = userStorage.findAll().stream().filter(userFinde -> userFinde.getId() == otherId).findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Нет пользователя с ID: " + otherId));
        HashSet<Integer> otherSet = new HashSet<>(userOne.getFriends());
        otherSet.retainAll(userTwo.getFriends());
        for (Integer friend : otherSet) {
            friendsList.add(userStorage.findAll().stream().filter(user1 -> user1.getId() == friend).findFirst().get());
        }
        ArrayList<User> arrayUser = new ArrayList<>(friendsList);
        Collections.sort(arrayUser, new UserComparator());
        log.info("Выведены общине друзья пользователя");
        return arrayUser;
    }
}
