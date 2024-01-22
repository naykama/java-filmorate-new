package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements IMUserStorage {
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
        }
        log.info("Список пользователей выведен, сейчас их количество: {}", users.size());
        return users.get(id);
    }


    @Override
    public User post(User user) {
        user.setId(incrementId());
        users.put(user.getId(), user);
        log.info("{} был добавлен к списку пользователей", user.getName());
        return user;
    }

    @Override
    public User put(User user) {
        if (!users.containsKey(user.getId())) {
            throw new EntityNotFoundException("Нет пользователя с ID: " + id);
        } else {
            users.put(user.getId(), user);
            log.info("\"{}\" пользователь под данным id был обновлен", user.getName());
            return user;
        }
    }

    private int incrementId() {
        return id++;
    }

}
