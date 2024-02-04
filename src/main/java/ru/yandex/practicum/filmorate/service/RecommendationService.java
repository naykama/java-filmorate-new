package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.RecommendationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final UserService userService;
    private final FilmService filmService;
    private final JdbcTemplate jdbcTemplate;

    public Set<Film> getRecommendedFilms(Integer userId) {
        Map<Integer, List<Integer>> filmsUsers = new HashMap<>();
        //Ищем всех пользователей
        List<User> userList = userService.findAll();
        if (userList.isEmpty()) {
            log.error("В базе нет ниодного пользователя!");
            throw new RecommendationException("В базе нет ниодного пользователя!");
        }
        //получение всех фильмов, которые пользователь лайкнул
        for (User user : userList) {
            filmsUsers.put(user.getId(), getUsersFilms(user.getId()));
            if (filmsUsers.isEmpty()) {
                log.error("В базе нет фильмов с лайками!");
                throw new RecommendationException("В базе нет фильмов с лайками!");
            }
        }
        //Находим пользователей с максимальным количеством пересечения по лайкам.
        long maxMatches = 0;
        Set<Integer> similarFilms = new HashSet<>();
        for (Integer id : filmsUsers.keySet()) {
            if (id.equals(userId)) {
                continue;
            }

            long countOfMatches = filmsUsers.get(id).stream()
                    .filter(filmId -> filmsUsers.get(userId).contains(filmId)).count();

            if (countOfMatches == maxMatches) {
                similarFilms.add(id);
            }
            if (countOfMatches > maxMatches & countOfMatches != 0) {
                maxMatches = countOfMatches;
                similarFilms = new HashSet<>();
                similarFilms.add(id);
            }
        }
        if (maxMatches == 0) {
            return new HashSet<>();
        } else {
            //Определяем фильм, который один пользователь пролайкал, а другой нет.
            return similarFilms.stream().flatMap(idUser -> getUsersFilms(idUser).stream())
                    .filter(filmId -> !filmsUsers.get(userId).contains(filmId))
                    .map(filmService::findFimById)
                    .collect(Collectors.toSet());
        }
    }

    //Ищем фильмы которым пользователи поставили лайки
    private List<Integer> getUsersFilms(Integer userId) {
        if (userId == 0) {
            log.error("Передан неверный аргумент" + userId);
            throw new RecommendationException("Передан неверный аргумент" + userId);
        }
        String sql = "SELECT id_film FROM film_liks WHERE id_user = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("id_film"), userId);
    }
}
