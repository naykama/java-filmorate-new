package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

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
    public User findUserById(Optional<Integer> id) {
        if (!users.containsKey(id.get())) {
            throw new EntityNotFoundException("Нет пользователя с ID: " + id);
        }
        log.info("Список пользователей выведен, сейчас их количество: {}", users.size());
        return users.get(id.get());
    }


    @Override
    public User post(User user) {
        user.setId(incrementId());
//        if (user.getName() == null || user.getName().isBlank()) {
//            user.setName(user.getLogin());
//        }
        checkValidName(user);
        users.put(user.getId(), user);
        log.info("{} был добавлен к списку пользователей", user.getName());
        return user;
    }

    @Override
    public User put(User user) {
        if (!users.containsKey(user.getId())) {
            throw new EntityNotFoundException("Нет пользователя с ID: " + id);
        } else {
            checkValidName(user);
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
    private void checkValidName(User user){
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

}
