package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmDbStorageImpl implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserService userService;

    @Override
    public List<Film> findAll() {
        String sql = "select f.*, m.name as mpa_name from films f join mpa m on f.mpa = m.id order by f.id";
        List<Film> filmList = jdbcTemplate.query(sql, filmRowMapper());
        addGenresList(filmList);
        return filmList;
    }

    @Override
    public Film findFimById(int id) {
        String sql = "select f.*, m.name as mpa_name from films f join mpa m on f.mpa = m.id where f.id = ? order by f.id";
        try {
            List<Film> filmList = List.of(jdbcTemplate.queryForObject(sql, filmRowMapper(), id));
            addGenresList(filmList);
            return filmList.get(0);
        } catch (EmptyResultDataAccessException e) {
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
        film.setId(id.intValue());
        setGenresForFilm(film);
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
        return film;
    }

    public List<Film> popular(int count) {
        String sql = "select f.*, m.name as mpa_name from films f join mpa m on f.mpa = m.id order by rate desc limit ?";
        if (count == 10) {
            return jdbcTemplate.query(sql, filmRowMapper(), 10);
        } else {
            return jdbcTemplate.query(sql, filmRowMapper(), count);
        }
    }


    private RowMapper<Film> filmRowMapper() {
        return (rs, rowNum) -> {
            Film film = new Film(rs.getInt("id"), rs.getString("name"), rs.getString("description"), LocalDate.parse(rs.getString("release_date")), rs.getInt("duration"));
            film.setRate(rs.getInt("rate"));
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("mpa"));
            mpa.setName(rs.getString("mpa_name"));
            film.setMpa(mpa);
            return film;
        };
    }

    private void addGenresList(List<Film> filmList) {
        Map<Integer, List<Genre>> genreMap = new HashMap<>();
        List<Integer> filmId = new ArrayList<>();
        for (Film film : filmList) {
            filmId.add(film.getId());
        }
        String inSql = String.join(",", Collections.nCopies(filmId.size(), "?"));
        String sql = "select * from genres g inner join filme_genres fg on fg.genre_id =g.id where fg.film_id in (%s)";
        jdbcTemplate.query(String.format(sql, inSql), filmId.toArray(), (RowMapper<Film>) (rs, rowNum) -> {
            int filmIdKey = rs.getInt("film_id");
            int genreId = rs.getInt("id");
            String genreName = rs.getString("name");
            genreMap.computeIfAbsent(filmIdKey, key -> new ArrayList<>()).add(new Genre(genreId, genreName));
            return null;
        });
        for (Film film : filmList) {
            if (genreMap.isEmpty()) {
                film.setGenres(new LinkedHashSet<>());
            } else {
                film.setGenres(new LinkedHashSet<>(genreMap.get(film.getId())));
            }
        }
    }

    private Mpa updateMpa(int mpaId) {
        Mpa newMpa = new Mpa();
        String mpaName = jdbcTemplate.queryForObject("SELECT name FROM mpa WHERE id = ?", new Integer[]{mpaId}, String.class);
        newMpa.setId(mpaId);
        newMpa.setName(mpaName);
        return newMpa;
    }

    public void addLike(int idFilm, int idUser) {
        String sqlInsert = "insert into film_liks (id_user,id_film) values (?,?)";
        String sqlUpdate = "update films set rate = (rate + 1) where id = ?";
        Film filmLik = findFimById(idFilm);
        User userLike = userService.findUserById(idUser);
        jdbcTemplate.update(sqlInsert, idUser, idFilm);
        jdbcTemplate.update(sqlUpdate, idFilm);
    }

    public void dellLike(int idFilm, int idUser) {
        String sqlDell = "DELETE FROM film_liks WHERE id_user = ? AND id_film = ?";
        String sqlUpdate = "update films set rate = (rate - 1) where id = ?";
        Film filmLik = findFimById(idFilm);
        User userLike = userService.findUserById(idUser);
        jdbcTemplate.update(sqlDell, idUser, idFilm);
        jdbcTemplate.update(sqlUpdate, idFilm);
    }


    private void setGenresForFilm(Film film) {
        String sqlDell = "DELETE FROM filme_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlDell, film.getId());
        jdbcTemplate.update(sqlDell, film.getId());

        List<Object[]> batchList = new ArrayList<>();

        for (Genre genre : film.getGenres()) {
            batchList.add(new Object[]{film.getId(), genre.getId()});
        }
        insertGenresForFilm(batchList);
        List<Film> listFilm = List.of(film);
        addGenresList(listFilm);
    }

    public void insertGenresForFilm(List<Object[]> batchUpdate) {
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
}
