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

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FilmService filmService;

    @GetMapping()
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable int id) {
        return userService.findUserById(id);
    }

    @PostMapping()
    public User post(@Valid @RequestBody User user) {
        return userService.post(user);
    }

    @PutMapping()
    public User put(@Valid @RequestBody User user) {
        return userService.put(user);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public void addFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.addFriends(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void dellFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.dellFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getEventsForUserByID(@PathVariable Integer id) {
        return userService.getEventsForUserByID(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendation(@PathVariable Integer id) {
        return filmService.getRecommendedFilms(id);
    }

    @DeleteMapping("/{id}")
    public User delete(@PathVariable Integer id) {
        return userService.delete(id);
    }

    @GetMapping("/{id}/recommendationsByMarks")
    public List<Film> getRecommendationByMarks(@PathVariable Integer id) {
        return filmService.getRecommendedByMarksFilms(id);
    }
}
