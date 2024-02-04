package ru.yandex.practicum.filmorate.dao;

import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    void createEvent(Event event);

    List<Event> getEventsForUserByID(int id);
}
