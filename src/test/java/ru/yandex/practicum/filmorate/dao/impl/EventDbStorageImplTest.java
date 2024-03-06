package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.EventStorage;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.model.Event.*;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class EventDbStorageImplTest {
    private final EventStorage eventStorage;
    private final FilmService filmService;
    private final UserService userService;
    private final ReviewService reviewService;

    @Test
    public void createAndGetEventForLikesTest() {
        Film film = filmService.findFimById(1);
        User user = userService.findUserById(1);
        filmService.addLike(film.getId(), user.getId());
        List<Event> events = eventStorage.getEventsForUserByID(user.getId());
        Event expectedEvent1 = createEvent(1, new Event(user.getId(), film.getId(), EventType.LIKE, OperationType.ADD));
        assertEquals(1, events.size());

        filmService.dellLike(film.getId(), user.getId());
        events = eventStorage.getEventsForUserByID(user.getId());
        Event expectedEvent2 = createEvent(2, new Event(user.getId(), film.getId(), EventType.LIKE, OperationType.REMOVE));
        assertEquals(2, events.size());
    }

    @Test
    public void createAndGetEventForFriendsTest() {
        User user = userService.findUserById(1);
        User friend = userService.findUserById(2);
        userService.addFriends(user.getId(), friend.getId());
        List<Event> events = eventStorage.getEventsForUserByID(user.getId());
        Event expectedEvent1 = createEvent(1, new Event(user.getId(), friend.getId(), EventType.FRIEND,
                                                                                       OperationType.ADD));
        assertEquals(1, events.size());

        userService.dellFriends(user.getId(), friend.getId());
        events = eventStorage.getEventsForUserByID(user.getId());
        Event expectedEvent2 = createEvent(2, new Event(user.getId(), friend.getId(), EventType.FRIEND,
                                                                                    OperationType.REMOVE));
        assertEquals(2, events.size());
    }

    @Test
    public void createAndGetEventForReviewsTest() {
        User user = userService.findUserById(1);
        Film film = filmService.findFimById(1);
        Review review = Review.builder().reviewId(1).content("Фильм не понравился!").isPositive(false)
                .userId(user.getId()).filmId(film.getId()).build();
        reviewService.addReview(review);
        List<Event> events = eventStorage.getEventsForUserByID(user.getId());
        Event expectedEvent1 = createEvent(1, new Event(user.getId(), review.getReviewId(), EventType.REVIEW,
                                                                                            OperationType.ADD));

        assertEquals(1, events.size());

        reviewService.updateReview(Review.builder().reviewId(1).content("Фильм понравился!").isPositive(true)
                .userId(user.getId()).filmId(film.getId()).build());
        events = eventStorage.getEventsForUserByID(user.getId());
        Event expectedEvent2 = createEvent(2, new Event(user.getId(), review.getReviewId(), EventType.REVIEW,
                                                                                         OperationType.UPDATE));

        assertEquals(2, events.size());

        reviewService.deleteReviewById(review.getReviewId());
        events = eventStorage.getEventsForUserByID(user.getId());
        Event expectedEvent3 = createEvent(3, new Event(user.getId(), review.getReviewId(), EventType.REVIEW,
                                                                                         OperationType.REMOVE));

        assertEquals(3, events.size());
    }

    @BeforeEach
    private void addDataToDB() {
        Film film = new Film(1, "film1", "filmDescr", LocalDate.now(), 30);
        film.setMpa(new Mpa(1, "G"));
        filmService.createFilm(film);
        User user = new User(1, "user1@mail.ru", "user", "user", LocalDate.of(2000, 3,12));
        userService.post(user);
        userService.post(new User(2, "friend@mail.ru", "friend", "", LocalDate.of(1999,
                4, 16)));
    }

    private Event createEvent(int id, Event event) {
        event.setEventId(id);
        return event;
    }
}
