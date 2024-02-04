package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReviewDbStorageImpl implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review getReviewById(int id) {

        String sql = "SELECT * FROM reviews WHERE id=?";
        List<Review> reviews = jdbcTemplate.query(sql, this::mapRowToReview, id);
        if (reviews.isEmpty()) {
            throw new EntityNotFoundException("Отзыв с таким id не найден");
        } else {
            return reviews.get(0);
        }
    }

    @Override
    public Review addReview(Review review) {

        String sql1 = "SELECT EXISTS (SELECT 1 FROM films WHERE id=?)";
        String sql2 = "SELECT EXISTS (SELECT 1 FROM users WHERE id=?)";

        if (jdbcTemplate.queryForObject(sql1, Boolean.class, review.getFilmId())
                && jdbcTemplate.queryForObject(sql2, Boolean.class, review.getUserId())) {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                    .withTableName("reviews").usingGeneratedKeyColumns("id");
            Integer id = simpleJdbcInsert.executeAndReturnKey(review.toMap()).intValue();
            review.setReviewId(id.intValue());
            return review;
        } else throw new EntityNotFoundException("Невалидный id пользователя или фильма");
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE reviews SET content=?, is_positive=? WHERE id=?";

        String sql2 = "SELECT user_id, film_id, useful FROM reviews WHERE id=?";
        Map<String, Object> map = jdbcTemplate.queryForMap(sql2, review.getReviewId());
        int oldUserId = (int) map.get("user_id");
        int oldFilmId = (int) map.get("film_id");
        int oldUsefulCount = (int) map.get("useful");

        int rows = jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());

        if (rows > 0) {
            review.setUserId(oldUserId);
            review.setFilmId(oldFilmId);
            review.setUseful(oldUsefulCount);
            return review;
        } else throw new IllegalArgumentException("Такой отзыв не найден");
    }

    @Override
    public void deleteReviewById(int id) {
        String sql = "DELETE FROM reviews WHERE id=?";
        int rowsAffected = jdbcTemplate.update(sql, id);

        if (rowsAffected == 0) {
            throw new IllegalArgumentException("Отзыв с таким id отсутствует");
        }
    }

    @Override
    public List<Review> getAllReviews() {
        String sql = "SELECT * FROM reviews ORDER BY useful DESC";
        return jdbcTemplate.query(sql, this::mapRowToReview);
    }

    @Override
    public List<Review> getReviewsByFilmId(int filmId, int count) {
        String sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT(?)";
        return jdbcTemplate.query(sql, this::mapRowToReview, filmId, count);
    }

    @Override
    public void addLikeToReview(int id, int userId) {
        Review review = getReviewById(id);
        int rating = review.getUseful();
        review.setUseful(++rating);
        String sql = "UPDATE reviews SET useful=? WHERE id=?";
        jdbcTemplate.update(sql, review.getUseful(), review.getReviewId());

    }

    @Override
    public void addDislikeToReview(int id, int userId) {
        Review review = getReviewById(id);
        int rating = review.getUseful();
        review.setUseful(--rating);
        String sql = "UPDATE reviews SET useful=? WHERE id=?";
        jdbcTemplate.update(sql, review.getUseful(), review.getReviewId());
    }

    @Override
    public void deleteLikeToReview(int id, int userId) {
        Review review = getReviewById(id);
        int rating = review.getUseful();
        review.setUseful(--rating);
        String sql = "UPDATE reviews SET useful=? WHERE id=?";
        jdbcTemplate.update(sql, review.getUseful(), review.getReviewId());
    }

    @Override
    public void deleteDislikeToReview(int id, int userId) {
        Review review = getReviewById(id);
        int rating = review.getUseful();
        review.setUseful(++rating);
        String sql = "UPDATE reviews SET useful=? WHERE id=?";
        jdbcTemplate.update(sql, review.getUseful(), review.getReviewId());
    }

    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getInt("id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getInt("user_id"))
                .filmId(rs.getInt("film_id"))
                .useful(rs.getInt("useful"))
                .build();
    }
}
