package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping()
    public List<Mpa> mpaFindAll() {
        List<Mpa> mpaList = mpaService.mpaFindAll();
        log.info("Список рейтингов выведен, их количество \"{}\"", mpaList.size());
        return mpaList;
    }

    @GetMapping("/{id}")
    public Mpa mpaFindAll(@PathVariable int id) {
        Mpa mpa = mpaService.findMpaForId(id);
        log.info("Рейтинг под номером \"{}\" выведен", mpa.getId());
        return mpa;
    }
}
