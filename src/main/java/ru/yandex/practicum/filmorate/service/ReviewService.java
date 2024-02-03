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
        log.info("Получен отзыв с id {}", id);
        return reviewStorage.getReviewById(id);
    }

    public Review addReview(Review review) {
        log.info("Добавлен отзыв {}", review.toString());
        reviewStorage.addReview(review);
        return review;
    }

    public Review updateReview(Review review) {
        log.info("Обновлен отзыв {}", review.toString());
        return reviewStorage.updateReview(review);
    }

    public void deleteReviewById(int id) {
        log.info("Удален отзыв с id {}", id);
        reviewStorage.deleteReviewById(id);
    }


    public List<Review> getAllReviews() {
        log.info("Получены все отзывы");
        return reviewStorage.getAllReviews();
    }

    public List<Review> getReviewsByFilmId(int filmId, int count) {
        log.info("Получено {} отзывов для фильма с id {}", count, filmId);
        return reviewStorage.getReviewsByFilmId(filmId, count);
    }

    public void addLikeToReview(int id, int userId) {
        log.info("Добавлен лайк отзыву с id {} от пользователя с id {}", id, userId);
        reviewStorage.addLikeToReview(id, userId);
    }

    public void addDislikeToReview(int id, int userId) {
        log.info("Добавлен дизлайк отзыву с id {} от пользователя с id {}", id, userId);
        reviewStorage.addDislikeToReview(id, userId);
    }

    public void deleteLikeToReview(int id, int userId) {
        log.info("Удален лайк отзыву с id {} от пользователя с id {}", id, userId);
        reviewStorage.deleteLikeToReview(id, userId);
    }

    public void deleteDislikeToReview(int id, int userId) {
        log.info("Удален дизлайк отзыву с id {} от пользователя с id {}", id, userId);
        reviewStorage.deleteDislikeToReview(id, userId);
    }
}
