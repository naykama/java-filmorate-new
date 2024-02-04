package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenresStorage;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenresDbStorageImpl implements GenresStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> genresFindAll() {
        String sql = "select * from genres order by id";
        return jdbcTemplate.query(sql, genreRowMapper());
    }

    @Override
    public Genre genresFindForId(int id) {
        String sql = "select * from genres where id = ? order by id";
        try {
            return jdbcTemplate.queryForObject(sql, genreRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка номера жанра \"{}\"", id);
            throw new EntityNotFoundException("Нет жанра с id: " + id);
        }
    }

    public void load(List<Film> films) {
            final Map<Integer, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));
            String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
            final String sqlQuery = "select * from GENRES g, FILME_GENRES fg where fg.GENRE_ID = g.ID  AND fg.FILM_ID  in (" + inSql + ")";
            jdbcTemplate.query(sqlQuery, (rs, rowNum) -> {
                final Film film = filmById.get(rs.getInt("FILM_ID")); 
                film.getGenres().add(makeGenre(rs, 0));
                return film;
            }, films.stream().map(Film::getId).toArray());
    }

    private Genre makeGenre(ResultSet rs, int columnIndex) throws SQLException {
        int genreId = rs.getInt(columnIndex + 1);
        String genreName = rs.getString("NAME");
        Genre genre = new Genre(genreId, genreName);
        return genre;
    }

    private RowMapper<Genre> genreRowMapper() {
        return (rs, rowNum) -> new Genre(rs.getInt("id"), rs.getString("name"));
    }
}
