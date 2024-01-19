package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.GenresDbStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenresController {
    private final GenresDbStorage genresDbStorage;

    @GetMapping()
    public List<Genre> genresFindAll() {
        return genresDbStorage.genresFindAll();
    }

    @GetMapping("/{id}")
    public Genre genresFindAll(@PathVariable int id) {
        return genresDbStorage.genresFindForId(id);
    }


}
