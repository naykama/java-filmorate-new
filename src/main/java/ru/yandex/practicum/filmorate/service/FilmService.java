package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mark;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Event.*;

import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenresStorage genresStorage;
    private final UserStorage userStorage;
    private final DirectorStorage directorStorage;
    private final EventStorage eventStorage;

    public List<Film> findAll() {
        List<Film> filmList = filmStorage.findAll();
        genresStorage.load(filmList);
        directorStorage.load(filmList);
        log.info("Список фильмов выведен, их количество \"{}\"", filmList.size());
        return filmList;
    }

    public Film createFilm(Film film) {
        Film createdFilm = filmStorage.createFilm(film);
        log.info("Фильм под номером \"{}\" добавлен", createdFilm.getId());
        return createdFilm;
    }

    public Film findFimById(int id) {
        List<Film> filmList = List.of(filmStorage.findFimById(id));
        genresStorage.load(filmList);
        directorStorage.load(filmList);
        log.info("Фильм под номером \"{}\" выведен", filmList.get(0).getId());
        return filmList.get(0);
    }

    public Film updateFilm(Film film) {
        Film updatedFilm = filmStorage.updateFilm(film);
        log.info("Фильм под номером \"{}\" обновлен", updatedFilm.getId());
        return updatedFilm;
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> filmList = filmStorage.getPopularFilms(count);
        genresStorage.load(filmList);
        log.info("Выведен список популярных фильмов");
        return filmList;
    }

    public List<Film> getPopularFilmsByMarks(int count) {
        List<Film> filmList = filmStorage.getPopularFilmsByMarks(count);
        genresStorage.load(filmList);
        directorStorage.load(filmList);
        log.info("Выведен список популярных фильмов");
        return filmList;
    }

    public List<Film> getPopularFilmsForGenreByMarks(int genre, int count) {
        List<Film> filmList = filmStorage.getPopularFilmsForGenreByMarks(genre, count);
        genresStorage.load(filmList);
        directorStorage.load(filmList);
        log.info("Выполнен GET запрос на получение самых популярных фильмов по жанру");
        return filmList;
    }

    public List<Film> getPopularFilmsForYearByMarks(int year, int count) {
        List<Film> filmList = filmStorage.getPopularFilmsForYearByMarks(year, count);
        genresStorage.load(filmList);
        directorStorage.load(filmList);
        log.info("Выполнен GET запрос на получение самых популярных фильмов по году");
        return filmList;
    }

    public List<Film> getPopularFilmsForGenreAndYearByMarks(int year, int genreId, int count) {
        List<Film> filmList = filmStorage.getPopularFilmsForGenreAndYearByMarks(year, genreId, count);
        genresStorage.load(filmList);
        directorStorage.load(filmList);
        log.info("Выполнен GET запрос на получение самых популярных фильмов по жанру и году");
        return filmList;
    }

    public List<Film> getMostLikedFilmsByGenreAndYear(int count, Integer genreId, Integer year) {
        List<Film> filmList;
        if (genreId == null) {
            genreId = 0;
            filmList = popular(count, genreId, year);
        } else if (year == null) {
            year = 0;
            filmList = popular(count, genreId, year);
        } else {
            filmList = popular(count, genreId, year);
        }
        log.info("Получен GET запрос на получение самых популярных фильмов по жанру и году");
        return filmList;
    }

    public List<Film> popular(int count, int genreId, int year) {
        List<Film> filmList = filmStorage.getMostLikedFilmsByGenreAndYear(count, genreId, year);
        genresStorage.load(filmList);
        directorStorage.load(filmList);
        return  filmList;
    }

    public void addLike(int id, int userId) {
        User userLike = userStorage.findUserById(userId);
        filmStorage.addLike(id, userId);
        eventStorage.createEvent(new Event(userId, id, EventType.LIKE, OperationType.ADD));
        log.info("Фильму под номером \"{}\", поставил лайк, пользователь под номером \"{}\"", id, userId);
    }

    public void dellLike(int id, int userId) {
        User userLike = userStorage.findUserById(userId);
        filmStorage.dellLike(id, userId);
        eventStorage.createEvent(new Event(userId, id, EventType.LIKE, OperationType.REMOVE));
        log.info("Фильму под номером \"{}\", удалили лайк, пользователь под номером \"{}\"", id, userId);
    }


    public List<Film> getFilmsForDirectorSortedByLikes(int directorId) {
        directorStorage.findDirectorById(directorId);
        List<Film> filmList = filmStorage.getFilmsForDirectorSortedByLikes(directorId);
        genresStorage.load(filmList);
        directorStorage.load(filmList);
        log.info("Выведен список фильмов режиссёра с id = \"{}\", отсортированный по количеству лайков", directorId);
        return filmList;
    }

    public List<Film> getFilmsForDirectorSortedByYear(int directorId) {
        directorStorage.findDirectorById(directorId);
        List<Film> filmList = filmStorage.getFilmsForDirectorSortedByYear(directorId);
        genresStorage.load(filmList);
        directorStorage.load(filmList);
        log.info("Выведен список фильмов режиссёра с id = \"{}\", отсортированный по году выпуска", directorId);
        return filmList;
    }

    public List<Film> getFilmsForDirectorSortedByMark(int directorId) {
        directorStorage.findDirectorById(directorId);
        List<Film> filmList = filmStorage.getFilmsForDirectorSortedByMark(directorId);
        genresStorage.load(filmList);
        directorStorage.load(filmList);
        log.info("Выведен список фильмов режиссёра с id = \"{}\", отсортированный по оценкам", directorId);
        return filmList;
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        List<Film> filmList = filmStorage.getCommonFilms(userId, friendId);
        log.info("Выведен список совместных фильмов пользователей под id \"{}\" и \"{}\", размер списка: \"{}\"", userId,
                friendId, filmList.size());
        return filmList;
    }

    public List<Film> search(String query, String by) {
        List<Film> filmList = filmStorage.search(query, by);
        genresStorage.load(filmList);
        directorStorage.load(filmList);
        log.info("Выведен список фильмов согласно поиску, по запросу \"{}\"", query);
        return filmList;
    }

    public Film delete(Integer filmId) {
        log.info("Получен DELETE-запрос к эндпоинту: '/films' на удаление фильма с ID={}", filmId);
        return filmStorage.delete(filmId);
    }

    public List<Film> getRecommendedFilms(Integer userId) {
        List<Film> filmList = new ArrayList<>(filmStorage.getRecommendedFilms(userId));
        genresStorage.load(filmList);
        log.info("Список рекомендованных фильмов пользователю, \"{}\"", userId);
        return filmList;
    }

    public void addMark(int id, int userId, int mark) {
        User userLike = userStorage.findUserById(userId);
        filmStorage.addMark(id, userId, mark);
        log.info("Фильму под номером {}, поставил оценку {} пользователь под номером {}", id, mark, userId);
    }

    public List<Film> getRecommendedByMarksFilms(Integer userId) {
        if (userId == null) {
            log.error("В метод getRecommendedByMarksFilms передан пустой аргумент");
            throw new EntityNotFoundException("Передан пустой аргумент!");
        }
        Map<Integer, Mark> marksForMainUser = new HashMap<>();
        Map<Integer, List<Mark>> marksForEachUser = new HashMap<>();
        filmStorage.fillMapsForUsers(userId, marksForMainUser, marksForEachUser);
        int userIdForRecommend = findUserForRecommendation(userId, marksForMainUser, marksForEachUser);
        if (userIdForRecommend == 0) {
            return new ArrayList<>();
        }
        List<Film> filmList = new ArrayList<>(filmStorage.getFilmsForRecommendation(marksForMainUser,
                                                                        marksForEachUser.get(userIdForRecommend)));
        genresStorage.load(filmList);
        directorStorage.load(filmList);
        log.info("Список рекомендованных фильмов пользователю, \"{}\"", userId);
        return filmList;
    }

    private int findUserForRecommendation(int userId, Map<Integer, Mark> marksForMainUser, Map<Integer,
            List<Mark>> marksForEachUser) {
        Map<Integer, Double> diffMainUserAndOthers = new HashMap<>();
        for (int currentUserId : marksForEachUser.keySet()) {
            int commonFilmsCount = 0;
            for (Mark mark : marksForEachUser.get(currentUserId)) {
                if (marksForMainUser.containsKey(mark.getFilmId())) {
                    commonFilmsCount++;
                    diffMainUserAndOthers.put(currentUserId,
                            diffMainUserAndOthers.getOrDefault(currentUserId, 0.0)
                                    + (marksForMainUser.get(mark.getFilmId()).getMark() - mark.getMark()));
                }
            }
            diffMainUserAndOthers.put(currentUserId,
                    diffMainUserAndOthers.getOrDefault(currentUserId, 0.0) / commonFilmsCount);
        }
        int recommendUserId = 0;
        double minDiff = Double.MAX_VALUE;
        for (int currentUserId : diffMainUserAndOthers.keySet()) {
            if (minDiff > Math.abs(diffMainUserAndOthers.get(currentUserId))) {
                minDiff = Math.abs(diffMainUserAndOthers.get(currentUserId));
                recommendUserId = currentUserId;
            }
        }
        return recommendUserId;
    }
}
