package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenresService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenresController {
    private final GenresService genresService;

    @GetMapping()
    public List<Genre> genresFindAll() {
        List<Genre> genreList = genresService.genresFindAll();
        log.info("Список жанров выведен, их количество \"{}\"", genreList.size());
        return genresService.genresFindAll();
    }

    @GetMapping("/{id}")
    public Genre genresFindAll(@PathVariable int id) {
        Genre mpa = genresService.genresFindForId(id);
        log.info("Жанр под номером \"{}\" выведен", mpa.getId());
        return genresService.genresFindForId(id);
    }


}
