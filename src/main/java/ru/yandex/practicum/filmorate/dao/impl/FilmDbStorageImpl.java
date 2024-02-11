package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
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

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAll() {
        String sql = "select f.*, m.name as mpa_name from films f join mpa m on f.mpa = m.id order by f.id";
        List<Film> filmList = jdbcTemplate.query(sql, filmRowMapper());
        return filmList;
    }

    @Override
    public Film findFimById(int id) {
        String sql = "select f.*, m.name as mpa_name from films f join mpa m on f.mpa = m.id where f.id = ? order by f.id";
        try {
            List<Film> filmList = List.of(jdbcTemplate.queryForObject(sql, filmRowMapper(), id));
            return filmList.get(0);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка поиска фильма с id \"{}\"", id);
            throw new EntityNotFoundException("Нет фильма с id: " + id);
        }
    }

    @Override
    public Film post(Film film) {
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
    public Film put(Film film) {
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
        String sql = "select f.*, m.name as mpa_name from films f join mpa m on f.mpa = m.id order by rate desc limit ?";
        return jdbcTemplate.query(sql, filmRowMapper(), count);
    }

    @Override
    public List<Film> getMostLikedFilmsByGenreAndYear(int count, int genreID, int year) {
        String sqlYear = "SELECT f.id, f.name, f.rate, f.description, f.release_date, f.duration, f.mpa, m.name mpa_name " +
                "FROM films f " +
                "JOIN mpa m ON m.id = f.mpa " +
                "LEFT JOIN film_liks l on f.id = l.id_film " +
                "WHERE Extract(year from cast(f.release_date as date)) = ?" +
                "GROUP BY f.id " +
                "ORDER BY count(l.id_user) desc " +
                "limit ?;";
        String sqlGenre = "SELECT f.id, f.name, f.rate, f.description, f.release_date, f.duration, f.mpa, m.name mpa_name " +
                "FROM films f " +
                "JOIN mpa m ON m.id = f.mpa " +
                "LEFT JOIN film_liks l on f.id = l.id_film " +
                "JOIN filme_genres fg on f.id = fg.film_id " +
                "WHERE fg.genre_id = ?" +
                "GROUP BY f.id " +
                "ORDER BY count(l.id_user) desc " +
                "limit ?;";
        String sqlYearAndGenre = "SELECT f.id, f.name, f.rate, f.description, f.release_date, f.duration, f.mpa, m.name mpa_name " +
                "FROM films f " +
                "LEFT JOIN film_liks l on f.id = l.id_film " +
                "JOIN mpa m ON m.id = f.mpa " +
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
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f INNER JOIN film_liks fl1 ON f.id = fl1.id_film AND fl1.id_user = ? INNER JOIN film_liks fl2 ON f.id = fl2.id_film AND fl2.id_user = ? INNER JOIN mpa m ON m.id = f.mpa ORDER BY f.rate DESC";
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
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f INNER JOIN mpa m ON m.id = f.mpa INNER JOIN film_director fd ON f.id = fd.film_id INNER JOIN directors d ON fd.director_id = d.id WHERE d.name ILIKE CONCAT('%', ?, '%')";
        return jdbcTemplate.query(sql, filmRowMapper(), query);
    }

    public List<Film> searchByTitle(String query) {
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f INNER JOIN mpa m ON m.id = f.mpa WHERE f.name ILIKE CONCAT('%', ?, '%')";
        return jdbcTemplate.query(sql, filmRowMapper(), query);
    }

    public List<Film> searchByDirectorAndTitle(String query, List<String> byList) {
        String sql = "select f.*, m.name as mpa_name from films f inner join mpa m on m.id = f.mpa where f.name ILIKE CONCAT('%', ?, '%') or f.name = (select f.name from films f inner join film_director fd on f.id = fd.film_id inner join directors d on fd.director_id = d.id where d.name ILIKE CONCAT('%', ?, '%')) order by rate desc";
        if (byList.contains("title") && byList.contains("director") && byList.size() == 2) {
            return jdbcTemplate.query(sql, filmRowMapper(), query, query);
        } else {
            log.error("Указан некорректный параметр запроса");
            throw new IllegalRequestParameterException("Некорректный параметр запроса");
        }
    }

    @Override
    public List<Film> getFilmsForDirectorSortedByLikes(int directorId) {
        String sql = "SELECT f.*, m.NAME as mpa_name FROM FILMS f\n" +
                "join mpa m on m.id = f.mpa\n" +
                "LEFT JOIN FILM_DIRECTOR fd ON f.id = fd.FILM_ID\n" +
                "WHERE fd.DIRECTOR_ID = ?\n" +
                "ORDER BY f.RATE;";
        List<Film> filmList = jdbcTemplate.query(sql, filmRowMapper(), directorId);
        return filmList;
    }

    @Override
    public List<Film> getFilmsForDirectorSortedByYear(int directorId) {
        String sql = "SELECT f.*, m.NAME as mpa_name FROM FILMS f\n" +
                "join mpa m on m.id = f.mpa\n" +
                "LEFT JOIN FILM_DIRECTOR fd ON f.id = fd.FILM_ID\n" +
                "WHERE fd.DIRECTOR_ID = ?\n" +
                "ORDER BY YEAR(release_date);";
        List<Film> filmList = jdbcTemplate.query(sql, filmRowMapper(), directorId);
        return filmList;
    }

}
