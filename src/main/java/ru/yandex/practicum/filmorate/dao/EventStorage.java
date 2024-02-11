package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    void createEvent(Event event);

    List<Event> getEventsForUserByID(int id);
}
