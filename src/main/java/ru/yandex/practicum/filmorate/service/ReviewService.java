package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventStorage;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.Event.*;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final EventStorage eventStorage;

    public Review getReviewById(int id) {
        Review review = reviewStorage.getReviewById(id);
        log.info("Получен отзыв с id {}", id);
        return review;
    }

    public Review addReview(Review review) {
        reviewStorage.addReview(review);
        eventStorage.createEvent(new Event(review.getUserId(), review.getReviewId(), EventType.REVIEW, OperationType.ADD));
        log.info("Добавлен отзыв {}", review.toString());
        return review;
    }

    public Review updateReview(Review review) {
        Review updatedReview = reviewStorage.updateReview(review);
        eventStorage.createEvent(new Event(review.getUserId(), review.getReviewId(), EventType.REVIEW, OperationType.UPDATE));
        log.info("Обновлен отзыв {}", review.toString());
        return updatedReview;
    }

    public void deleteReviewById(int id) {
        int userId = reviewStorage.getReviewById(id).getUserId();
        reviewStorage.deleteReviewById(id);
        eventStorage.createEvent(new Event(userId, id, EventType.REVIEW, OperationType.REMOVE));
        log.info("Удален отзыв с id {}", id);
    }


    public List<Review> getAllReviews() {
        List<Review> reviews = reviewStorage.getAllReviews();
        log.info("Получены все отзывы");
        return reviews;
    }

    public List<Review> getReviewsByFilmId(int filmId, int count) {
        List<Review> reviews = reviewStorage.getReviewsByFilmId(filmId, count);
        log.info("Получено {} отзывов для фильма с id {}", count, filmId);
        return reviews;
    }

    public void addLikeToReview(int id, int userId) {
        reviewStorage.addLikeToReview(id, userId);
        log.info("Добавлен лайк отзыву с id {} от пользователя с id {}", id, userId);
    }

    public void addDislikeToReview(int id, int userId) {
        reviewStorage.addDislikeToReview(id, userId);
        log.info("Добавлен дизлайк отзыву с id {} от пользователя с id {}", id, userId);
    }

    public void deleteLikeToReview(int id, int userId) {
        reviewStorage.deleteLikeToReview(id, userId);
        log.info("Удален лайк отзыву с id {} от пользователя с id {}", id, userId);
    }

    public void deleteDislikeToReview(int id, int userId) {
        reviewStorage.deleteDislikeToReview(id, userId);
        log.info("Удален дизлайк отзыву с id {} от пользователя с id {}", id, userId);
    }
}
