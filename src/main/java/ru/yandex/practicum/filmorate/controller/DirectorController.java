package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public List<Director> findAll() {
        List<Director> directorList = directorService.findAll();
        log.info("Список режиссёров выведен, их количество \"{}\"", directorList.size());
        return directorList;
    }

    @GetMapping("/{id}")
    public Director findDirectorById(@PathVariable int id) {
        Director director = directorService.findDirectorById(id);
        log.info("Режиссёр под номером \"{}\" выведен", director.getId());
        return director;
    }

    @PostMapping
    public Director post(@Valid @RequestBody Director director) {
        Director directorPost = directorService.post(director);
        log.info("Режиссёр под номером \"{}\" добавлен", director.getId());
        return directorPost;
    }

    @PutMapping
    public Director put(@Valid @RequestBody Director director) {
        Director directorPut = directorService.put(director);
        log.info("Режиссёр под номером \"{}\" обновлен", director.getId());
        return directorPut;
    }

    @DeleteMapping("/{id}")
    public void delDirectorById(@PathVariable int id) {
        directorService.delDirectorById(id);
        log.info("Режиссёр под номером \"{}\" удален", id);
    }
}
