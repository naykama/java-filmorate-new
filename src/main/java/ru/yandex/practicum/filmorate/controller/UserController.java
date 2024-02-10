package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FilmService filmService;

    @GetMapping()
    public List<User> findAll() {
        List<User> userList = userService.findAll();
        log.info("Список юзеров выведен, их количество \"{}\"", userList.size());
        return userList;
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable int id) {
        User user = userService.findUserById(id);
        log.info("Юзер под номером \"{}\" выведен", user.getId());
        return user;
    }

    @PostMapping()
    public User post(@Valid @RequestBody User user) {
        User userPost = userService.post(user);
        log.info("Юрез под номером \"{}\" добавлен", user.getId());
        return userPost;
    }

    @PutMapping()
    public User put(@Valid @RequestBody User user) {
        User userPut = userService.put(user);
        log.info("Юрез под номером \"{}\" обновлен", user.getId());
        return userPut;
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public void addFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.addFriends(id, friendId);
        log.info("Пользователь \"{}\", добавил в друзь пользователя \"{}\"", id, friendId);

    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void dellFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.dellFriends(id, friendId);
        log.info("Пользователь \"{}\", удалил из друзей пользователя \"{}\"", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) {
        List<User> userList = userService.getFriends(id);
        log.info("Список друзей пользователя \"{}\", размером \"{}\"", id, userList.size());
        return userList;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        List<User> userList = userService.getCommonFriends(id, otherId);
        log.info("Список общих друзей пользователя \"{}\" и \"{}\", размером \"{}\"", id, otherId, userList.size());
        return userList;
    }

    @GetMapping("/{id}/feed")
    public List<Event> getEventsForUserByID(@PathVariable Integer id) {
        List<Event> eventList = userService.getEventsForUserByID(id);
        log.info("Cписок событий пользователя \"{}\", размером \"{}\"", id, eventList.size());
        return eventList;
    }

    @GetMapping("/{id}/recommendations")
    public Set<Film> getRecommendation(@PathVariable Integer id) {
        log.info("Список рекомендованных фильмов пользователю, \"{}\"", id);
        return filmService.getRecommendedFilms(id);
    }

    @DeleteMapping("/{id}")
    public User delete(@PathVariable Integer id) {
        log.info("Получен DELETE-запрос к эндпоинту: '/users' на удаление юзера с ID={}", id);
        return userService.delete(id);
    }
}
