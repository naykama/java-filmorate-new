package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.GenresStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenresStorage genresStorage;
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    public List<Film> findAll() {
        List<Film> filmList = filmStorage.findAll();
        genresStorage.load(filmList);
        return filmList;
    }

    public Film post(Film film) {
        return filmStorage.post(film);
    }

    public Film findFimById(int id) {
        List<Film> filmList = List.of(filmStorage.findFimById(id));
        genresStorage.load(filmList);
        return filmList.get(0);
    }

    public Film put(Film film) {
        return filmStorage.put(film);
    }

    public List<Film> popular(int count) {
        return filmStorage.popular(count);
    }

    public void addLike(int id, int userId) {
        String sqlInsert = "insert into film_liks (id_user,id_film) values (?,?)";
        String sqlUpdate = "update films set rate = (rate + 1) where id = ?";
        Film filmLik = findFimById(id);
        User userLike = userStorage.findUserById(userId);
        jdbcTemplate.update(sqlInsert, userId, id);
        jdbcTemplate.update(sqlUpdate, id);
    }

    public void dellLike(int id, int userId) {
        String sqlDell = "DELETE FROM film_liks WHERE id_user = ? AND id_film = ?";
        String sqlUpdate = "update films set rate = (rate - 1) where id = ?";
        Film filmLik = findFimById(id);
        User userLike = userStorage.findUserById(userId);
        jdbcTemplate.update(sqlDell, userId, id);
        jdbcTemplate.update(sqlUpdate, id);
    }
}
