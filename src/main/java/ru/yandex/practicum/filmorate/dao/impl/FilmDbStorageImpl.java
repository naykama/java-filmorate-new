package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.exeption.IllegalRequestParameterException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmDbStorageImpl implements FilmStorage {
    private static final short MAX_BAD_MARK = 5;
    private final JdbcTemplate jdbcTemplate;
    private final String sqlToGetFilm = "SELECT f.*, m.name as mpa_name, mark_rate.average_rate\n" +
            "FROM films f\n" +
            "JOIN mpa m ON f.mpa = m.id\n" +
            "LEFT JOIN (SELECT ma.film_id AS film_id, AVG(ma.MARK) AS average_rate\n" +
            "FROM MARKS ma\n" +
            "GROUP BY ma.FILM_ID) AS mark_rate ON f.id = mark_rate.film_id\n";

    @Override
    public List<Film> findAll() {
        return jdbcTemplate.query(sqlToGetFilm + "order by f.id;", filmRowMapper());
    }

    @Override
    public Film findFimById(int id) {
        try {
            return jdbcTemplate.queryForObject(sqlToGetFilm + "where f.id = ? order by f.id;", filmRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка поиска фильма с id \"{}\"", id);
            throw new EntityNotFoundException("Нет фильма с id: " + id);
        }
    }

    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource()).withTableName("films").usingGeneratedKeyColumns("id");
        film.setMpa(updateMpa(film.getMpa().getId()));
        film.setRate(0);
        Map<String, Object> params = Map.of("name", film.getName(), "description", film.getDescription(), "release_date", film.getReleaseDate().toString(), "duration", film.getDuration(), "rate", film.getRate(), "mpa", film.getMpa().getId());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        film.setRate(0);
        film.setId(id.intValue());
        setGenresForFilm(film);
        deleteAndSetDirectorsForFilm(film);
        return film;
    }


    @Override
    public Film updateFilm(Film film) {
        String sqlUpdate = "update films set  name = ?, description = ?,release_date = ?,duration = ?,rate = ?, mpa = ? where id = ?";
        Film findFilm = findFimById(film.getId());
        film.setRate(findFilm.getRate());
        jdbcTemplate.update(sqlUpdate, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getRate(), film.getMpa().getId(), film.getId());
        film.setMpa(updateMpa(film.getMpa().getId()));
        setGenresForFilm(film);
        deleteAndSetDirectorsForFilm(film);
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        return jdbcTemplate.query(sqlToGetFilm + "order by rate desc limit ?", filmRowMapper(), count);
    }

    @Override
    public List<Film> getPopularFilmsByMarks(int count) {
        return jdbcTemplate.query(sqlToGetFilm + "order by mark_rate.average_rate desc limit ?",
                                    filmRowMapper(), count);
    }

    @Override
    public List<Film> getPopularFilmsForGenreByMarks(int genreId, int count) {
        return jdbcTemplate.query(sqlToGetFilm + "LEFT JOIN filme_genres AS fg ON f.id = fg.film_id\n" +
                "WHERE fg.genre_id = ? limit ?", filmRowMapper(), genreId, count);
    }

    @Override
    public List<Film> getPopularFilmsForYearByMarks(int year, int count) {
        return jdbcTemplate.query(sqlToGetFilm + "WHERE YEAR(release_date) = ? limit ?",
                                    filmRowMapper(), year, count);
    }

    @Override
    public List<Film> getPopularFilmsForGenreAndYearByMarks(int year, int genreId, int count) {
        return jdbcTemplate.query(sqlToGetFilm + "LEFT JOIN filme_genres AS fg ON f.id = fg.film_id\n" +
                "WHERE YEAR(release_date) = ? AND fg.genre_id = ? limit ?", filmRowMapper(), year, genreId, count);
    }

    @Override
    public List<Film> getMostLikedFilmsByGenreAndYear(int count, int genreID, int year) {
        String sqlYear = sqlToGetFilm +
                "LEFT JOIN film_liks l on f.id = l.id_film " +
                "WHERE Extract(year from cast(f.release_date as date)) = ?" +
                "GROUP BY f.id " +
                "ORDER BY count(l.id_user) desc " +
                "limit ?;";
        String sqlGenre = sqlToGetFilm +
                "LEFT JOIN film_liks l on f.id = l.id_film " +
                "JOIN filme_genres fg on f.id = fg.film_id " +
                "WHERE fg.genre_id = ?" +
                "GROUP BY f.id " +
                "ORDER BY count(l.id_user) desc " +
                "limit ?;";
        String sqlYearAndGenre = sqlToGetFilm +
                "JOIN filme_genres fg on f.id = fg.film_id " +
                "WHERE fg.genre_id = ? and Extract(year from cast(f.release_date as date)) = ?" +
                "GROUP BY f.id " +
                "ORDER BY count(l.id_user) desc " +
                "limit ?;";

        if (genreID != 0 && year != 0) {
            return jdbcTemplate.query(sqlYearAndGenre, filmRowMapper(), genreID, year, count);
        }
        if (genreID == 0) {
            return jdbcTemplate.query(sqlYear, filmRowMapper(), year, count);
        }
        if (year == 0) {
            return jdbcTemplate.query(sqlGenre, filmRowMapper(), genreID, count);
        }
        return Collections.emptyList();
    }


    public void addLike(int id, int userId) {
        String sqlInsert = "MERGE INTO film_liks (id_user,id_film) VALUES (?, ?)";
        String sqlUpdate = "update films set rate = (rate + 1) where id = ?";
        Film filmLik = findFimById(id);
        jdbcTemplate.update(sqlInsert, userId, id);
        jdbcTemplate.update(sqlUpdate, id);
    }

    public void dellLike(int id, int userId) {
        String sqlDell = "DELETE FROM film_liks WHERE id_user = ? AND id_film = ?";
        String sqlUpdate = "update films set rate = (rate - 1) where id = ?";
        Film filmLik = findFimById(id);
        jdbcTemplate.update(sqlDell, userId, id);
        jdbcTemplate.update(sqlUpdate, id);
    }

    @Override
    public Set<Film> getRecommendedFilms(Integer userId) {
        if (userId == null) {
            throw new EntityNotFoundException("Передан пустой аргумент!");
        }
        Map<Integer, List<Integer>> filmsOfUser = new HashMap<>();
        List<Integer> userList = jdbcTemplate.query(
                "SELECT id FROM users",
                (rs, rowNum) -> rs.getInt("id"));
        if (userList.isEmpty()) {
            throw new EntityNotFoundException("В базе нет ниодного пользователя!");
        }
        String sql = "SELECT id_user, id_film FROM film_liks WHERE id_film IN (SELECT id_film FROM film_liks)";
        jdbcTemplate.query(sql, (ResultSet rs) -> {
            while (rs.next()) {
                int idUser = rs.getInt("id_user");
                int filmId = rs.getInt("id_film");
                if (!filmsOfUser.containsKey(idUser)) {
                    filmsOfUser.put(idUser, new ArrayList<>());
                }
                filmsOfUser.get(idUser).add(filmId);
            }
            return null;
        });
        long maxMatches = 0;
        Set<Integer> similarFilms = new HashSet<>();
        for (Integer id : filmsOfUser.keySet()) {
            if (id.equals(userId)) {
                continue;
            }
            long countOfMatches = filmsOfUser.get(id).stream()
                    .filter(filmsOfUser.get(userId)::contains).count();
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
            return similarFilms.stream().flatMap(idUser -> getFilmListLikes(idUser).stream())
                    .filter(filmId -> !filmsOfUser.get(userId).contains(filmId))
                    .map(this::findFimById)
                    .collect(Collectors.toSet());
        }
    }

    private List<Integer> getFilmListLikes(Integer userId) {
        return jdbcTemplate.query(
                "SELECT id_film FROM film_liks WHERE id_user = ?",
                (rs, rowNum) ->
                        rs.getInt("id_film"), userId);
    }

    private RowMapper<Film> filmRowMapper() {
        return (rs, rowNum) -> {
            Film film = new Film(rs.getInt("id"), rs.getString("name"),
                    rs.getString("description"), LocalDate.parse(rs.getString("release_date")),
                    rs.getInt("duration"));
            film.setRate(rs.getInt("rate"));
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("mpa"));
            mpa.setName(rs.getString("mpa_name"));
            film.setMpa(mpa);
            if (rs.getString("AVERAGE_RATE") != null) {
                film.setMarkRate(rs.getDouble("AVERAGE_RATE"));
            }
            return film;
        };
    }

    private Mpa updateMpa(int mpaId) {
        Mpa newMpa = new Mpa();
        String mpaName = jdbcTemplate.queryForObject("SELECT name FROM mpa WHERE id = ?", new Integer[]{mpaId}, String.class);
        newMpa.setId(mpaId);
        newMpa.setName(mpaName);
        return newMpa;
    }

    private void setGenresForFilm(Film film) {
        String sqlDell = "DELETE FROM filme_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlDell, film.getId());

        List<Object[]> batchList = new ArrayList<>();

        for (Genre genre : film.getGenres()) {
            batchList.add(new Object[]{film.getId(), genre.getId()});
        }
        insertGenresForFilm(batchList);
    }

    private void insertGenresForFilm(List<Object[]> batchUpdate) {
        String sql = "insert into filme_genres (film_id, genre_id) select ?, ? where not exists (select 1 from filme_genres where film_id = ? and genre_id = ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                Object[] parameters = batchUpdate.get(i);
                preparedStatement.setInt(1, (int) parameters[0]);
                preparedStatement.setInt(2, (int) parameters[1]);
                preparedStatement.setInt(3, (int) parameters[0]);
                preparedStatement.setInt(4, (int) parameters[1]);
            }

            @Override
            public int getBatchSize() {
                return batchUpdate.size();
            }
        });
    }

    @Override
    public Film delete(Integer filmId) {
        if (filmId == null) {
            throw new EntityNotFoundException("Передан пустой аргумент!");
        }

        Film film = findFimById(filmId);
        String sqlQueryF = "DELETE FROM films WHERE id = ? ";
        jdbcTemplate.update(sqlQueryF, filmId);
        return film;
    }

    private void deleteAndSetDirectorsForFilm(Film film) {
        String sql = "DELETE FROM film_director WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getId());

        List<Object[]> batchList = new ArrayList<>();
        for (Director director : film.getDirectors()) {
            batchList.add(new Object[]{film.getId(), director.getId()});
        }
        insertDirectorsForFilm(batchList);
    }

    private void insertDirectorsForFilm(List<Object[]> filmDirectorIdList) {
        String sql = "MERGE INTO film_director VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                Object[] parameters = filmDirectorIdList.get(i);
                preparedStatement.setInt(1, (int) parameters[0]);
                preparedStatement.setInt(2, (int) parameters[1]);
            }

            @Override
            public int getBatchSize() {
                return filmDirectorIdList.size();
            }
        });
    }

    public List<Film> getСommonFilms(int userId, int friendId) {
        String sql = sqlToGetFilm + "INNER JOIN film_liks fl1 ON f.id = fl1.id_film AND fl1.id_user = ?\n" +
                "INNER JOIN film_liks fl2 ON f.id = fl2.id_film AND fl2.id_user = ?\n" +
                "ORDER BY f.rate DESC";
        return jdbcTemplate.query(sql, filmRowMapper(), userId, friendId);
    }

    @Override
    public List<Film> search(String query, String by) {
        List<String> byList = new ArrayList<>();
        if (by.contains(",")) {
            String[] values = by.split(",");
            for (String value : values) {
                byList.add(value);
            }
            return searchByDirectorAndTitle(query, byList);
        } else {
            switch (by) {
                case "director":
                    return searchByDirector(query);
                case "title":
                    return searchByTitle(query);
                default:
                    log.error("Указан некорректный параметр запроса \"{}\"", by);
                    throw new IllegalRequestParameterException("Некорректный параметр запроса");
            }
        }
    }

    public List<Film> searchByDirector(String query) {
        String sql = sqlToGetFilm + "INNER JOIN film_director fd ON f.id = fd.film_id\n" +
                "INNER JOIN directors d ON fd.director_id = d.id\n"
                + "WHERE d.name ILIKE CONCAT('%', ?, '%')";
        return jdbcTemplate.query(sql, filmRowMapper(), query);
    }

    public List<Film> searchByTitle(String query) {
        return jdbcTemplate.query(sqlToGetFilm + "WHERE f.name ILIKE CONCAT('%', ?, '%')", filmRowMapper(), query);
    }

    public List<Film> searchByDirectorAndTitle(String query, List<String> byList) {
        String sql = sqlToGetFilm +
                "LEFT JOIN film_director fd ON f.id = fd.film_id\n" +
                "lEFT JOIN directors d ON fd.director_id = d.id\n" +
                "WHERE d.name ILIKE CONCAT('%', ?, '%') OR f.name ILIKE CONCAT('%', ?, '%')\n" +
                "Order by rate desc";
        if (byList.contains("title") && byList.contains("director") && byList.size() == 2) {
            return jdbcTemplate.query(sql, filmRowMapper(), query, query);
        } else {
            log.error("Указан некорректный параметр запроса");
            throw new IllegalRequestParameterException("Некорректный параметр запроса");
        }
    }

    @Override
    public List<Film> getFilmsForDirectorSortedByLikes(int directorId) {
        String sql = sqlToGetFilm +
                "LEFT JOIN FILM_DIRECTOR fd ON f.id = fd.FILM_ID\n" +
                "WHERE fd.DIRECTOR_ID = ?\n" +
                "ORDER BY f.RATE";
        List<Film> filmList = jdbcTemplate.query(sql, filmRowMapper(), directorId);
        return filmList;
    }

    @Override
    public List<Film> getFilmsForDirectorSortedByYear(int directorId) {
        String sql = sqlToGetFilm +
                "LEFT JOIN FILM_DIRECTOR fd ON f.id = fd.FILM_ID\n" +
                "WHERE fd.DIRECTOR_ID = ?\n" +
                "ORDER BY YEAR(release_date)";
        List<Film> filmList = jdbcTemplate.query(sql, filmRowMapper(), directorId);
        return filmList;
    }

    @Override
    public List<Film> getFilmsForDirectorSortedByMark(int directorId) {
        return jdbcTemplate.query(sqlToGetFilm +
                "LEFT JOIN FILM_DIRECTOR fd ON f.id = fd.FILM_ID\n" +
                "WHERE fd.DIRECTOR_ID = ?\n" +
                "ORDER BY average_rate", filmRowMapper(), directorId);
    }

    @Override
    public void addMark(int id, int userId, int mark) {
        try {
            jdbcTemplate.update("INSERT INTO marks (film_id, user_id, mark) VALUES (?, ?, ?)", id, userId, mark);
        } catch (RuntimeException e) {
            log.error("Ошибка при проставлении оценки пользователем с id = {} фильму с id = {}", userId, id);
            throw new IllegalRequestParameterException(String.format("Оценка фильму c id = %d уже проставлена автором " +
                    "c id = %d. Нужно сначала удалить оценку", id, userId));
        }
    }

    public Set<Film> getRecommendedByMarksFilms(Integer userId) {
        if (userId == null) {
            log.error("В метод getRecommendedByMarksFilms передан пустой аргумент");
            throw new EntityNotFoundException("Передан пустой аргумент!");
        }
        Map<Integer, Mark> marksForMainUser = new HashMap<>();
        Map<Integer, List<Mark>> marksForEachUser = new HashMap<>();
        fillMapsForUsers(userId, marksForMainUser, marksForEachUser);
        int userIdForRecommend = findUserForRecommendation(userId, marksForMainUser, marksForEachUser);
        return userIdForRecommend == 0 ? new HashSet<>()
                : getFilmsForRecommendation(marksForMainUser, marksForEachUser.get(userIdForRecommend));
    }

    private void fillMapsForUsers(int userId, Map<Integer, Mark> marksForMainUser, Map<Integer, List<Mark>> marksForEachUser) {
        SqlRowSet marksSet = jdbcTemplate.queryForRowSet("SELECT * FROM marks");
        while (marksSet.next()) {
            int currentUserId = marksSet.getInt("user_id");
            Mark mark = new Mark(marksSet.getInt("film_id"), currentUserId, marksSet.getInt("mark"));
            if (currentUserId == userId) {
                marksForMainUser.put(mark.getFilmId(), mark);;
            } else {
                if (marksForEachUser.containsKey(currentUserId)) {
                    marksForEachUser.get(currentUserId).add(mark);
                } else {
                    marksForEachUser.put(currentUserId, new ArrayList<>());
                    marksForEachUser.get(currentUserId).add(mark);
                }
            }
        }
    }

    private Set<Film> getFilmsForRecommendation(Map<Integer, Mark> marksForMainUser, List<Mark> marksForRecommendUser) {
        Set<Integer> filmIdForRecommend = marksForRecommendUser
                .stream()
                .filter(mark -> !marksForMainUser.containsKey(mark.getFilmId()) && mark.getMark() > MAX_BAD_MARK)
                .map(Mark::getFilmId)
                .collect(Collectors.toSet()
        );
        String parameters = String.join(",", Collections.nCopies(filmIdForRecommend.size(), "?"));
        List<Film> films = jdbcTemplate.query(
                String.format(sqlToGetFilm + "WHERE f.id IN (%s)",
                        parameters), filmIdForRecommend.toArray(), filmRowMapper());
        return  new HashSet<>(films);
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
