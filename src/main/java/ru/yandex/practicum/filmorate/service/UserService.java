package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventStorage;
import ru.yandex.practicum.filmorate.dao.FriendsUserStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
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
        List<User> userList = userStorage.findAll();
        log.info("Список юзеров выведен, их количество \"{}\"", userList.size());
        return userList;
    }

    public User findUserById(int id) {
        User user = userStorage.findUserById(id);
        log.info("Юзер под номером \"{}\" выведен", user.getId());
        return user;
    }

    public User post(User user) {
        checkValidName(user);
        User userPost = userStorage.post(user);
        log.info("Юзер под номером \"{}\" добавлен", userPost.getId());
        return userPost;
    }

    public User put(User user) {
        checkValidName(user);
        User userPut = userStorage.put(user);
        log.info("Юрез под номером \"{}\" обновлен", userPut.getId());
        return userPut;
    }

    public void addFriends(Integer id, Integer friendId) {
        friendsUserStorage.addFriends(id, friendId);
        eventStorage.createEvent(new Event(id, friendId, EventType.FRIEND, OperationType.ADD));
        log.info("Пользователь \"{}\", добавил в друзь пользователя \"{}\"", id, friendId);
    }

    public void dellFriends(Integer id, Integer friendId) {
        friendsUserStorage.dellFriends(id, friendId);
        eventStorage.createEvent(new Event(id, friendId, EventType.FRIEND, OperationType.REMOVE));
        log.info("Пользователь \"{}\", удалил из друзей пользователя \"{}\"", id, friendId);
    }

    public List<User> getFriends(Integer id) {
        findUserById(id);
        List<User> userList = friendsUserStorage.getFriends(id);
        log.info("Список друзей пользователя \"{}\", размером \"{}\"", id, userList.size());
        return userList;
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        List<User> userList = friendsUserStorage.getCommonFriends(id, otherId);
        log.info("Список общих друзей пользователя \"{}\" и \"{}\", размером \"{}\"", id, otherId, userList.size());
        return userList;
    }

    public List<Event> getEventsForUserByID(int userId) {
        userStorage.findUserById(userId);
        List<Event> eventList = eventStorage.getEventsForUserByID(userId);
        log.info("Cписок событий пользователя \"{}\", размером \"{}\"", userId, eventList.size());
        return eventList;
    }

    private void checkValidName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public User delete(Integer userId) {
        log.info("Получен DELETE-запрос к эндпоинту: '/users' на удаление юзера с ID={}", userId);
        return userStorage.delete(userId);
    }
}
