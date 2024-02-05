package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.model.Director;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {
   private final DirectorStorage directorStorage;

    public List<Director> findAll() {
        return directorStorage.findAll();
    }

    public Director findDirectorById(int id) {
        return directorStorage.findDirectorById(id);
    }

    public Director post(Director director) {
        return directorStorage.post(director);
    }

    public Director put(Director director) {
        return directorStorage.put(director);
    }

    public void delDirectorById(int id) {
        directorStorage.delDirectorById(id);
    }
}
