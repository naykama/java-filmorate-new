package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public Mpa findMpaForId(int id) {
        Mpa mpa = mpaStorage.findMpaForId(id);
        log.info("Рейтинг под номером \"{}\" выведен", mpa.getId());
        return mpa;
    }

    public List<Mpa> mpaFindAll() {
        List<Mpa> mpaList = mpaStorage.mpaFindAll();
        log.info("Список рейтингов выведен, их количество \"{}\"", mpaList.size());
        return mpaList;
    }
}
