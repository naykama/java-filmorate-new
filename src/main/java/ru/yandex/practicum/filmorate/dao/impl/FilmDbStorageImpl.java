package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FilmDbStorageImpl implements FilmDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;

    @Override
    public List<Film> findAll() {
        String sql = "select * from films";
        return jdbcTemplate.query(sql, filmRowMapper());
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

        for (int i = 0; i < film.getGenres().size(); i++) {
            if(!film.getGenres().contains(film.getGenres().get(i))) {
                film.getGenres().get(i).setName(genresAdd(film.getGenres().get(i).getId()));
            }
        }

        Map<String, Object> params = Map.of(
                "name", film.getName(),
                "description", film.getDescription(),
                "release_date", film.getReleaseDate().toString(),
                "duration", film.getDuration(),
                "rate", film.getRate(),
                "mpa", film.getMpa().getId()
        );

        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        film.setId(id.intValue());

        String sqlDell= "DELETE FROM filme_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlDell,film.getId());
        for (Genre genre : film.getGenres()) {
            insertGenreForFilm(film.getId(), genre.getId());
        }
        film.setGenres(addGenresList(film.getId()));
        return film;
    }

    @Override
    public Film findFimById(int id) {
        String sql = "select * from films where id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, filmRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Нет фильма с id: " + id);
        }
    }

    @Override
    public Film put(Film film) {
        String sqlFindFilm = "select * from films where id = ?";
        String sqlUpdate = "update films set  name = ?, description = ?,release_date = ?,duration = ?,rate = ?, mpa = ? where id = ?";
        Film findFilm;
        try {
            findFilm = jdbcTemplate.queryForObject(sqlFindFilm, filmRowMapper(), film.getId());
        } catch (EmptyResultDataAccessException e) {
            findFilm = null;
        }
        if (findFilm == null) {
            throw new EntityNotFoundException("Нет фильма с ID: " + film.getId());
        }
//        for (int i = 0; i < film.getGenres().size(); i++) {
//            if(!film.getGenres().contains(film.getGenres().get(i))) {
//                film.getGenres().get(i).setName(genresAdd(film.getGenres().get(i).getId()));
//            }
//        }
        jdbcTemplate.update(sqlUpdate, film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getRate()
                , film.getMpa().getId()
                , film.getId());
        film.setMpa(updateMpa(film.getMpa().getId()));

        String sqlDell= "DELETE FROM filme_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlDell,film.getId());
        for (Genre genre : film.getGenres()) {
            insertGenreForFilm(film.getId(), genre.getId());
        }
        film.setGenres(addGenresList(film.getId()));
        return film;
    }

    public List<Film> popular(int count) {
        String sql = "select * from films order by rate desc limit ?;";
        if (count == 10) {
            return jdbcTemplate.query(sql, filmRowMapper(), 10);
        } else {
            return jdbcTemplate.query(sql, filmRowMapper(), count);
        }
    }

    private RowMapper<Film> filmRowMapper() {
        return (rs, rowNum)
                -> {
            Film film = new Film(rs.getInt("id")
                    , rs.getString("name")
                    , rs.getString("description")
                    , LocalDate.parse(rs.getString("release_date")),
                    rs.getInt("duration"));
            int mpaId = rs.getInt("mpa");
            film.setRate(rs.getInt("rate"));
            List<Genre> genreList = addGenresList(film.getId());
            film.setGenres(genreList);
            if (mpaId != 0) {
                film.setMpa(updateMpa(mpaId));
            }
            return film;
        };
    }

    public void addLike(int idFilm, int idUser) {
        String sqlInsert = "insert into film_liks (id_user,id_film) values (?,?)";
        String sqlUpdate = "update films set rate = (rate + 1) where id = ?";
        Film filmLik = findFimById(idFilm);
        User userLike = userDbStorage.findUserById(idUser);
        jdbcTemplate.update(sqlInsert, idUser, idFilm);
        jdbcTemplate.update(sqlUpdate, idFilm);
    }

    public void dellLike(int idFilm, int idUser) {
        String sqlDell = "DELETE FROM film_liks WHERE id_user = ? AND id_film = ?";
        String sqlUpdate = "update films set rate = (rate - 1) where id = ?";
        Film filmLik = findFimById(idFilm);
        User userLike = userDbStorage.findUserById(idUser);
        jdbcTemplate.update(sqlDell, idUser, idFilm);
        jdbcTemplate.update(sqlUpdate, idFilm);
    }

    private Mpa updateMpa(int mpaId) {
        Mpa newMpa = new Mpa();
        String mpaName = jdbcTemplate.queryForObject(
                "SELECT name FROM mpa WHERE id = ?",
                new Integer[]{mpaId},
                String.class
        );
        newMpa.setId(mpaId);
        newMpa.setName(mpaName);
        return newMpa;
    }

    private String genresAdd(int id) {
        return jdbcTemplate.queryForObject(
                "SELECT name FROM genres WHERE id = ?",
                (rs, rowNum) -> (rs.getString("name")),
                id);
    }

    private List<Genre> addGenresList(int id){
        String sql = "select * from genres g inner join filme_genres fg on fg.genre_id =g.id where fg.film_id = ?";
        return jdbcTemplate.query(sql,(rs, rowNum) -> new Genre(rs.getInt("id")
                ,rs.getString("name") ),id);
    }
    private void insertGenreForFilm(int idFilm, int idGenre) {
        String sql = "insert into filme_genres (film_id,genre_id) values (?,?) on conflict do nothing";
        jdbcTemplate.update(sql,idFilm,idGenre);
    }
}
