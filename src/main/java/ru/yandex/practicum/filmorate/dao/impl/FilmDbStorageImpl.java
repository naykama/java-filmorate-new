package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class FilmDbStorageImpl implements FilmDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> findAll() {
        String sql = "select * from films";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Film(rs.getInt("id"), rs.getString("name")
                , rs.getString("description"), LocalDate.parse(rs.getString("releaseDate")),
                rs.getInt("duration")));
    }

    @Override
    public Film post(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("films")
                .usingGeneratedKeyColumns("id");

        String mpaName = jdbcTemplate.queryForObject(
                "SELECT name FROM mpa WHERE id = ?",
                (rs, rowNum) -> (rs.getString("name")),
                film.getMpa().getId()
        );

        film.getMpa().setName(mpaName);

        Map<String, Object> params = Map.of(
                "name", film.getName(),
                "description", film.getDescription(),
                "releaseDate", film.getReleaseDate().toString(),
                "duration", film.getDuration(),
                "rate", film.getRate(),
                "mpa", film.getMpa().getId()
        );

        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        film.setId(id.intValue());
        return film;
    }

    @Override
    public Film findFimById(int id) {
        return null;
    }

    @Override
    public Film put(Film film) {
        String sqlFindFilm = "select * from films";
        String sqlUpdate = "";
        return null;
    }

//    private RowMapper<User> filmRowMapper() {
//
//    }
}
