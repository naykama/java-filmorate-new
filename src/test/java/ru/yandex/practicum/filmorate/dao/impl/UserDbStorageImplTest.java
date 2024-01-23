package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.FriendsUserStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserDbStorageImplTest {
    private final UserStorage userStorage;
    private final FriendsUserStorage friendsUserStorageDB;

    @Test
    void findAllTest() {
        User newUserOne = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        User newUserTwo = new User(2, "user2@email.ru", "petia123", "Petia Evanov", LocalDate.of(1993, 4, 12));
        UserService userService = new UserService(this.userStorage, friendsUserStorageDB);
        userService.post(newUserOne);
        userService.post(newUserTwo);
        List<User> userList = userService.findAll();
        assertFalse(userList.isEmpty());
        assertTrue(userList.size() == 2);
        assertEquals(userList.get(0), newUserOne);
        assertEquals(userList.get(1), newUserTwo);
    }

    @Test
    void findUserByIdTest() {
        User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        UserService userService = new UserService(this.userStorage, friendsUserStorageDB);
        userService.post(newUser);
        User savedUser = userService.findUserById(1);
        assertThat(savedUser).isNotNull().usingRecursiveComparison().isEqualTo(newUser);
        assertThrows(EntityNotFoundException.class, () -> userService.findUserById(9999));
    }


    @Test
    void postTest() {
        User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        UserService userService = new UserService(this.userStorage, friendsUserStorageDB);
        userService.post(newUser);
        User savedUser = userService.findUserById(1);
        assertNotNull(savedUser);
        assertEquals(newUser, savedUser);
        assertEquals(newUser.getLogin(), savedUser.getLogin());
    }

    @Test
    void putTest() {
        User newUserOne = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        User newUserTwo = new User(1, "user2@email.ru", "petia123", "Petia Evanov", LocalDate.of(1993, 4, 12));
        UserService userService = new UserService(this.userStorage, friendsUserStorageDB);
        userService.post(newUserOne);
        userService.put(newUserTwo);
        User savedUser = userService.findUserById(1);
        assertEquals(savedUser, newUserTwo);
    }

    @Test
    void addFriendsTest() {
        User newUserOne = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        User newUserTwo = new User(2, "user2@email.ru", "petia123", "Petia Evanov", LocalDate.of(1993, 4, 12));
        UserService userService = new UserService(this.userStorage, friendsUserStorageDB);
        userService.post(newUserOne);
        userService.post(newUserTwo);
        userService.addFriends(1, 2);
        List<User> friends = userService.getFriends(1);
        assertTrue(friends.size() == 1);
        assertEquals(friends.get(0), newUserTwo);
        assertThrows(EntityNotFoundException.class, () -> userService.addFriends(1, 9999));
    }

    @Test
    void dellFriendsTest() {
        User newUserOne = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        User newUserTwo = new User(2, "user2@email.ru", "petia123", "Petia Evanov", LocalDate.of(1993, 4, 12));
        UserService userService = new UserService(this.userStorage, friendsUserStorageDB);
        userService.post(newUserOne);
        userService.post(newUserTwo);
        userService.addFriends(1, 2);
        userService.dellFriends(1, 2);
        List<User> friends = userService.getFriends(1);
        assertTrue(friends.size() == 0);

    }

    @Test
    void getFriendsTest() {
        User newUserOne = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        User newUserTwo = new User(2, "user2@email.ru", "petia123", "Petia Evanov", LocalDate.of(1993, 4, 12));
        UserService userService = new UserService(this.userStorage, friendsUserStorageDB);
        userService.post(newUserOne);
        userService.post(newUserTwo);
        userService.addFriends(1, 2);
        List<User> friends = userService.getFriends(1);
        assertTrue(friends.size() == 1);
        assertEquals(friends.get(0), newUserTwo);

    }

    @Test
    void getCommonFriendsTest() {
        User newUserOne = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        User newUserTwo = new User(2, "user2@email.ru", "petia123", "Petia Evanov", LocalDate.of(1993, 4, 12));
        User newUserThree = new User(3, "user3@email.ru", "koliy123", "Kolia Fedorov", LocalDate.of(1998, 2, 22));
        UserService userService = new UserService(this.userStorage, friendsUserStorageDB);
        userService.post(newUserOne);
        userService.post(newUserTwo);
        userService.post(newUserThree);
        userService.addFriends(1, 3);
        userService.addFriends(2, 3);
        List<User> friends = userService.getCommonFriends(1, 2);
        assertTrue(friends.size() == 1);
        assertEquals(friends.get(0), newUserThree);
    }
}