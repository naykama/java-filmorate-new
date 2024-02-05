package ru.yandex.practicum.filmorate.exeption;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecommendationException extends IllegalArgumentException {
    public RecommendationException(String message) {
        log.error(message);
    }
}
