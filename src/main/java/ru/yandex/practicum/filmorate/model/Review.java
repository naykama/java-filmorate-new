package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Review {

    private int reviewId;

    @NotNull(message = "Отзыв должен содержать текст контента")
    private String content;

    @JsonProperty("isPositive")
    @NotNull(message = "Статус отзыва обязателен")
    private Boolean isPositive;

    @NotNull(message = "ID пользователя обязателен")
    private Integer userId;

    @NotNull(message = "ID фильма обязателен")
    private Integer filmId;

    private int useful;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("content", content);
        values.put("is_positive", isPositive);
        values.put("user_id", userId);
        values.put("film_id", filmId);
        values.put("useful", useful);
        return values;
    }
}
