package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.FriendsValidException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final InMemoryUserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriends(int id, int friendsId) {
        User user = userStorage.findAll().stream().filter(userFinde -> userFinde.getId() == id).findFirst()
                .orElseThrow(() -> new FriendsValidException("Нет пользователя с ID: " + id));
        if (!userStorage.findAll().stream().anyMatch(userForeEach -> userForeEach.getId() == friendsId)) {
            throw new FriendsValidException("Нет друга с таким id: " + friendsId);
        }
        user.getFriends().add(friendsId);
    }

    public void dellFriends(Integer id, Integer friendsId) {
        User user = userStorage.findAll().stream().filter(userFinde -> userFinde.getId() == id).findFirst()
                .orElseThrow(() -> new FriendsValidException("Нет пользователя с ID: " + id));
        if (!userStorage.findAll().stream().anyMatch(userForeEach -> userForeEach.getId() == friendsId)) {
            throw new FriendsValidException("Нет друга с таким id: " + friendsId);
        }
        user.getFriends().remove(friendsId);
    }
    public Set<User> getFriends(Integer id){
        Set<User> friendsList = new HashSet<>();
        User user = userStorage.findAll().stream().filter(userFinde -> userFinde.getId() == id).findFirst()
                .orElseThrow(() -> new FriendsValidException("Нет пользователя с ID: " + id));
        for (Integer friend : user.getFriends()) {
            friendsList.add(userStorage.findAll().stream().filter(user1 -> user1.getId()==friend).findFirst().get());
        }
        return friendsList;
    }
    public Set<User> getCommonFriends(Integer id, Integer otherId){
        Set<User> friendsList = new HashSet<>();
        User userOne = userStorage.findAll().stream().filter(userFinde -> userFinde.getId() == id).findFirst()
                .orElseThrow(() -> new FriendsValidException("Нет пользователя с ID: " + id));
        User userTwo = userStorage.findAll().stream().filter(userFinde -> userFinde.getId() == otherId).findFirst()
                .orElseThrow(() -> new FriendsValidException("Нет пользователя с ID: " + otherId));
        HashSet<Integer> otherSet = new HashSet<>(userOne.getFriends());
        otherSet.retainAll(userTwo.getFriends());
        for (Integer friend : otherSet) {
            friendsList.add(userStorage.findAll().stream().filter(user1 -> user1.getId()==friend).findFirst().get());
        }
        return friendsList;
    }

}
