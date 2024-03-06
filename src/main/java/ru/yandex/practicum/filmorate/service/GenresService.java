package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenresStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenresService {
    private final GenresStorage genresStorage;

    public List<Genre> genresFindAll() {
        List<Genre> genreList = genresStorage.genresFindAll();
        log.info("Список жанров выведен, их количество \"{}\"", genreList.size());
        return genreList;
    }

    public Genre genresFindForId(int id) {
        Genre mpa = genresStorage.genresFindForId(id);
        log.info("Жанр под номером \"{}\" выведен", mpa.getId());
        return mpa;
    }
}
