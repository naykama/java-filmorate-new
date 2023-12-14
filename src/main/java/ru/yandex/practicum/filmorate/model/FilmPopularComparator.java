package ru.yandex.practicum.filmorate.model;

import java.util.Comparator;

public class FilmPopularComparator implements Comparator<Film> {
    @Override
    public int compare(Film o1, Film o2) {
        return Integer.compare(o2.getRate(),o1.getRate());
    }
}
