package ru.yandex.practicum.filmorate.dao;


import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaDbStorage {
    Mpa findMpaForId(int id);
    List<Mpa> mpaFindAll();
}
