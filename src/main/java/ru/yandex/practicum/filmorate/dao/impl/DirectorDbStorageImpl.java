package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorDbStorageImpl implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> findAll() {
        String sql = "SELECT * FROM directors ORDER BY id";
        return jdbcTemplate.query(sql, directorRowMapper());
    }

    @Override
    public Director findDirectorById(int id) {
        String sql = "SELECT * FROM directors WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, directorRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка поиска режиссёра с id \"{}\"", id);
            throw new EntityNotFoundException("Нет режиссёра с id: " + id);
        }
    }

    @Override
    public Director post(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                        .withTableName("directors").usingGeneratedKeyColumns("id");
        Map<String, String> params = Map.of("name", director.getName());
        Number directorId = simpleJdbcInsert.executeAndReturnKey(params);
        director.setId(directorId.intValue());
        return director;
    }

    @Override
    public Director put(Director director) {
        String sql = "UPDATE directors SET name = ?";
        findDirectorById(director.getId());
        jdbcTemplate.update(sql, director.getName());
        return director;
    }

    @Override
    public void delDirectorById(int id) {
        String sql = "DELETE FROM film_director WHERE director_id = ?;\n" +
                "DELETE FROM directors WHERE id = ?";
        jdbcTemplate.update(sql, id, id);
    }

    @Override
    public void load(List<Film> films) {
        final Map<Integer, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));
        String parametersSql = String.join(",", Collections.nCopies(films.size(), "?"));
        final String sqlQuery = "SELECT * FROM directors d, film_director fd WHERE fd.director_id = d.id AND fd.film_id in ("
                                + parametersSql + ")";
        Integer[] filmIds = filmById.keySet().stream().toArray(Integer[]::new);
        SqlRowSet directorSet = jdbcTemplate.queryForRowSet(sqlQuery, filmIds);
        while (directorSet.next()) {
            filmById.get(directorSet.getInt("film_id")).getDirectors()
                    .add(new Director(directorSet.getInt("director_id"), directorSet.getString("name")));
        }
    }

    private RowMapper<Director> directorRowMapper() {
        return (rs, rowNum) -> new Director(rs.getInt("id"), rs.getString("name"));
    }
}
