package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewDbStorageImplTest {

    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testGetReviewById() {
        ReviewStorage reviewStorage = new ReviewDbStorageImpl(jdbcTemplate);
        FilmStorage filmStorage = new FilmDbStorageImpl(jdbcTemplate);
        UserStorage userStorage = new UserDbStorageImpl(jdbcTemplate);

        Review review = createFirstReview();
        Film film = createFirstFilm();
        User user = createFirstUser();

        Mpa mpaOne = new Mpa(1, "G");
        film.setMpa(mpaOne);

        filmStorage.createFilm(film);
        userStorage.post(user);
        reviewStorage.addReview(review);

        Review savedReview = reviewStorage.getReviewById(review.getReviewId());

        assertThat(savedReview)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(review);
    }

    @Test
    public void testUpdateReview() {
        ReviewStorage reviewStorage = new ReviewDbStorageImpl(jdbcTemplate);
        FilmStorage filmStorage = new FilmDbStorageImpl(jdbcTemplate);
        UserStorage userStorage = new UserDbStorageImpl(jdbcTemplate);

        Review review = createFirstReview();
        Film film = createFirstFilm();
        User user = createFirstUser();

        Mpa mpaOne = new Mpa(1, "G");
        film.setMpa(mpaOne);

        filmStorage.createFilm(film);
        userStorage.post(user);
        reviewStorage.addReview(review);

        review.setContent("Updated review");
        review.setIsPositive(false);

        Review updatedReview = reviewStorage.updateReview(review);

        assertThat(updatedReview)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(review);
    }

    @Test
    public void testDeleteReviewById() {
        ReviewStorage reviewStorage = new ReviewDbStorageImpl(jdbcTemplate);
        FilmStorage filmStorage = new FilmDbStorageImpl(jdbcTemplate);
        UserStorage userStorage = new UserDbStorageImpl(jdbcTemplate);

        Review review = createFirstReview();
        Film film = createFirstFilm();
        User user = createFirstUser();

        Mpa mpaOne = new Mpa(1, "G");
        film.setMpa(mpaOne);

        filmStorage.createFilm(film);
        userStorage.post(user);
        reviewStorage.addReview(review);

        reviewStorage.deleteReviewById(review.getReviewId());

        assertThatThrownBy(() -> reviewStorage.getReviewById(review.getReviewId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Отзыв с таким id не найден");
    }

    @Test
    public void testGetAllReviews() {
        ReviewStorage reviewStorage = new ReviewDbStorageImpl(jdbcTemplate);
        FilmStorage filmStorage = new FilmDbStorageImpl(jdbcTemplate);
        UserStorage userStorage = new UserDbStorageImpl(jdbcTemplate);

        Review reviewOne = createFirstReview();
        Review reviewTwo = createSecondReview();
        Film filmOne = createFirstFilm();
        Film filmTwo = createSecondFilm();
        User userOne = createFirstUser();
        User userTwo = createSecondUser();

        Mpa mpaOne = new Mpa(1, "G");
        filmOne.setMpa(mpaOne);
        filmTwo.setMpa(mpaOne);

        filmStorage.createFilm(filmOne);
        filmStorage.createFilm(filmTwo);
        userStorage.post(userOne);
        userStorage.post(userTwo);
        reviewStorage.addReview(reviewOne);
        reviewStorage.addReview(reviewTwo);

        List<Review> reviews = reviewStorage.getAllReviews();

        assertThat(reviews)
                .isNotNull()
                .hasSize(2)
                .containsExactlyInAnyOrder(reviewOne, reviewTwo);
    }

    @Test
    public void testGetReviewsByFilmId() {
        ReviewStorage reviewStorage = new ReviewDbStorageImpl(jdbcTemplate);
        FilmStorage filmStorage = new FilmDbStorageImpl(jdbcTemplate);
        UserStorage userStorage = new UserDbStorageImpl(jdbcTemplate);

        Review reviewOne = createFirstReview();
        Review reviewTwo = createSecondReview();
        Film filmOne = createFirstFilm();
        Film filmTwo = createSecondFilm();
        User userOne = createFirstUser();
        User userTwo = createSecondUser();

        Mpa mpaOne = new Mpa(1, "G");
        filmOne.setMpa(mpaOne);
        filmTwo.setMpa(mpaOne);

        filmStorage.createFilm(filmOne);
        filmStorage.createFilm(filmTwo);
        userStorage.post(userOne);
        userStorage.post(userTwo);
        reviewStorage.addReview(reviewOne);
        reviewStorage.addReview(reviewTwo);

        List<Review> reviewsByFilmOne = reviewStorage.getReviewsByFilmId(1, 10);

        assertThat(reviewsByFilmOne)
                .isNotNull()
                .hasSize(1)
                .containsExactly(reviewOne);

        List<Review> reviewsByFilmTwo = reviewStorage.getReviewsByFilmId(2, 10);

        assertThat(reviewsByFilmTwo)
                .isNotNull()
                .hasSize(1)
                .containsExactly(reviewTwo);
    }

    @Test
    public void testAddLikeToReview() {
        ReviewStorage reviewStorage = new ReviewDbStorageImpl(jdbcTemplate);
        FilmStorage filmStorage = new FilmDbStorageImpl(jdbcTemplate);
        UserStorage userStorage = new UserDbStorageImpl(jdbcTemplate);

        Review review = createFirstReview();
        Film film = createFirstFilm();
        User user = createFirstUser();

        Mpa mpaOne = new Mpa(1, "G");
        film.setMpa(mpaOne);

        filmStorage.createFilm(film);
        userStorage.post(user);
        reviewStorage.addReview(review);

        reviewStorage.addLikeToReview(review.getReviewId(), user.getId());

        Review likedReview = reviewStorage.getReviewById(review.getReviewId());

        assertThat(likedReview)
                .isNotNull()
                .hasFieldOrPropertyWithValue("useful", 1);
    }

    @Test
    public void testAddDislikeToReview() {
        ReviewStorage reviewStorage = new ReviewDbStorageImpl(jdbcTemplate);
        FilmStorage filmStorage = new FilmDbStorageImpl(jdbcTemplate);
        UserStorage userStorage = new UserDbStorageImpl(jdbcTemplate);

        Review review = createFirstReview();
        Film film = createFirstFilm();
        User user = createFirstUser();

        Mpa mpaOne = new Mpa(1, "G");
        film.setMpa(mpaOne);

        filmStorage.createFilm(film);
        userStorage.post(user);
        reviewStorage.addReview(review);

        reviewStorage.addDislikeToReview(review.getReviewId(), user.getId());

        Review dislikedReview = reviewStorage.getReviewById(review.getReviewId());

        assertThat(dislikedReview)
                .isNotNull()
                .hasFieldOrPropertyWithValue("useful", -1);
    }

    private Review createFirstReview() {
        return Review.builder()
                .reviewId(1)
                .content("Is positive review")
                .isPositive(true)
                .userId(1)
                .filmId(1)
                .useful(0)
                .build();
    }

    private Review createSecondReview() {
        return Review.builder()
                .reviewId(2)
                .content("Is negative review")
                .isPositive(false)
                .userId(2)
                .filmId(2)
                .useful(0)
                .build();
    }

    private Film createFirstFilm() {
        return new Film(1, "filmOne", "testDescription",
                LocalDate.of(2000, 12, 20), 167);
    }

    private Film createSecondFilm() {
        return new Film(2, "filmTwo", "testDescription",
                LocalDate.of(2000, 12, 20), 167);
    }

    private User createFirstUser() {
        return new User(1, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1));
    }

    private User createSecondUser() {
        return new User(2, "user2@email.ru", "petia123", "Petia Evanov",
                LocalDate.of(1993, 4, 12));
    }
}
