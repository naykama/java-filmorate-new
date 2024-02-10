package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable int id) {
        log.info("Получен запрос на вывод отзыва с id {}", id);
        return reviewService.getReviewById(id);
    }

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        reviewService.addReview(review);
        log.info("Получен запрос на добавление отзыва {}", review);
        return review;
    }

    @PutMapping
    public Review updateReview(@RequestBody Review review) {
        reviewService.updateReview(review);
        log.info("Получен запрос на обновление отзыв {}", review);
        return review;
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable int id) {
        reviewService.deleteReviewById(id);
        log.info("Получен запрос на удаление отзыва с id {}", id);
    }

    @GetMapping
    public List<Review> getAllReviewsByFilmId(@RequestParam(required = false) @Positive Integer filmId,
                                              @RequestParam(defaultValue = "10") int count) {
        log.info("Получен общий get-запрос");
        if (filmId == null) {
            log.info("Получен запрос на вывод всех отзывов");
            return reviewService.getAllReviews();

        } else {
            log.info("Получен запрос на вывод {} отзывов для фильма с id {}", count, filmId);
            return reviewService.getReviewsByFilmId(filmId, count);

        }
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToReview(@PathVariable int id, @PathVariable int userId) {
        reviewService.addLikeToReview(id, userId);
        log.info("Получен запрос на добавление лайка отзыву с id {} от пользователя с id {}", id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable int id, @PathVariable int userId) {
        reviewService.addDislikeToReview(id, userId);
        log.info("Получен запрос на добавление дизлайка обзору с id {} от пользователя с id {}", id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeToReview(@PathVariable int id, @PathVariable int userId) {
        reviewService.deleteLikeToReview(id, userId);
        log.info("Получен запрос на удаление лайка обзору с id {} от пользователя с id {}", id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeToReview(@PathVariable int id, @PathVariable int userId) {
        reviewService.deleteDislikeToReview(id, userId);
        log.info("Удален дизлайк обзору с id {} от пользователя с id {}", id, userId);
    }

    @ExceptionHandler
    public ResponseEntity<String> catchValidationException(ConstraintViolationException ex) {
        log.error("Возникла ошибка валидации входного значения");
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
