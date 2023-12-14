package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.UserFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private int id = 1;
    private static final List<User> users = new ArrayList<>();

    @Override
    public List<User> findAll() {
        log.info("Список пользователей выведен, сейчас их количество: " + users.size());
        return users;
    }

    @Override
    public User findUserById(int id) {
        User user = users.stream().filter(u -> u.getId() == id).findFirst()
                .orElseThrow(() -> new UserFoundException("Нет пользователя с ID: " + id));
        return user;
    }


    @Override
    public User post(User user) {
        user.setId(incrementId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.add(user);
        log.info("Пользователь добавлен: " + user.getName());
        return user;
    }

    @Override
    public User put(User user) {
        boolean userIdExist = users.stream().allMatch(userFoeEach -> userFoeEach.getId() == user.getId());
        if (!userIdExist) {
            throw new UserFoundException("Пользователь с указанным ID не найден");
        }
        users.removeIf(userFoeEach -> userFoeEach.getId() == user.getId());
        users.add(user);
        log.info("Пользователь обновлен: " + user.getName());
        return user;
    }

    private int incrementId() {
        return id++;
    }

}
