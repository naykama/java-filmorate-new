package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;

    public Review getReviewById(int id) {
        log.info("Передан запрос на получение отзыва с id {}", id);
        return reviewStorage.getReviewById(id);
    }

    public Review addReview(Review review) {
        log.info("Передан запрос на добавление отзыва {}", review.toString());
        reviewStorage.addReview(review);
        return review;
    }

    public Review updateReview(Review review) {
        log.info("Передан запрос на обновление отзыва {}", review.toString());
        return reviewStorage.updateReview(review);
    }

    public void deleteReviewById(int id) {
        reviewStorage.deleteReviewById(id);
        log.info("Удален отзыв с id {}", id);
    }


    public List<Review> getAllReviews() {
        log.info("Передан запрос на получение всех отзывов");
        return reviewStorage.getAllReviews();
    }

    public List<Review> getReviewsByFilmId(int filmId, int count) {
        log.info("Передан запрос на получение {} отзывов для фильма с id {}", count, filmId);
        return reviewStorage.getReviewsByFilmId(filmId, count);
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
