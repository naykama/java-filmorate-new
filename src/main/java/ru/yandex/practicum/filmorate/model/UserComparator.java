package ru.yandex.practicum.filmorate.model;

import java.util.Comparator;

public class UserComparator implements Comparator<User> {
    @Override
    public int compare(User o1, User o2) {
        return Integer.compare(o1.getId(),o2.getId());
    }
}
