package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaDbStorageImpl implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa findMpaForId(int id) {
        String sql = "select * from mpa where id = ? order by id";
        try {
            return jdbcTemplate.queryForObject(sql, mpaRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка номера рейтинга \"{}\"", id);
            throw new EntityNotFoundException("Нет рейтинга с id: " + id);
        }
    }

    @Override
    public List<Mpa> mpaFindAll() {
        String sql = "select * from mpa order by id";
        return jdbcTemplate.query(sql, mpaRowMapper());
    }

    private RowMapper<Mpa> mpaRowMapper() {
        return (rs, rowNum) -> new Mpa(rs.getInt("id"), rs.getString("name"));
    }
}
