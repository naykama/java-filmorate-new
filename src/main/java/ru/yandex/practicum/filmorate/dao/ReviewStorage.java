package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review getReviewById(int id);

    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReviewById(int id);

    List<Review> getAllReviews();

    List<Review> getReviewsByFilmId(int filmId, int count);

    void addLikeToReview(int id, int userId);

    void addDislikeToReview(int id, int userId);

    void deleteLikeToReview(int id, int userId);

    void deleteDislikeToReview(int id, int userId);
}
