package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private int id = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        log.info("Список пользователей выведен, сейчас их количество: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User findUserById(int id) {
        if (!users.containsKey(id)) {
            throw new EntityNotFoundException("Нет пользователя с ID: " + id);
        } else {
            log.info("Список пользователей выведен, сейчас их количество: {}", users.size());
            return users.get(id);
        }
//        User user = users.stream().filter(u -> u.getId() == id).findFirst()
//                .orElseThrow(() -> new EntityNotFoundException("Нет пользователя с ID: " + id));
//        log.info("Список пользователей выведен, сейчас их количество: {}", users.size());
    }


    @Override
    public User post(User user) {
        user.setId(incrementId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("{} был добавлен к списку пользователей", user.getName());
        return user;
    }

    @Override
    public User put(User user) {
//        boolean userIdExist = users.stream().allMatch(userFoeEach -> userFoeEach.getId() == user.getId());
//        if (!userIdExist) {
//            throw new EntityNotFoundException("Пользователь с указанным ID не найден");
//        }
        if (!users.containsKey(user.getId())) {
            throw new EntityNotFoundException("Нет пользователя с ID: " + id);
        } else {
            log.info("Список пользователей выведен, сейчас их количество: {}", users.size());
            users.remove(user.getId());
            users.put(user.getId(), user);
            log.info("\"{}\" пользователь под данным id был обновлен", user.getName());
            return user;
        }
    }

    private int incrementId() {
        return id++;
    }

    public Map<Integer, User> getUsers() {
        return users;
    }

}
