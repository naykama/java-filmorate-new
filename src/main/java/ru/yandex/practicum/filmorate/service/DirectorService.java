package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.model.Director;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
   private final DirectorStorage directorStorage;

    public List<Director> findAll() {
        List<Director> directorList = directorStorage.findAll();
        log.info("Список режиссёров выведен, их количество \"{}\"", directorList.size());
        return directorList;
    }

    public Director findDirectorById(int id) {
        Director director = directorStorage.findDirectorById(id);
        log.info("Режиссёр под номером \"{}\" выведен", director.getId());
        return director;
    }

    public Director createDirector(Director director) {
        Director savedDirector = directorStorage.createDirector(director);
        log.info("Режиссёр под номером \"{}\" добавлен", director.getId());
        return savedDirector;
    }

    public Director updateDirector(Director director) {
        Director updatedDirector = directorStorage.updateDirector(director);
        log.info("Режиссёр под номером \"{}\" обновлен", director.getId());
        return updatedDirector;
    }

    public void delDirectorById(int id) {
        directorStorage.delDirectorById(id);
        log.info("Режиссёр под номером \"{}\" удален", id);
    }
}
