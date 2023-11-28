package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Film {
    private int id;
    @NotBlank(message = "Имя не содержит символов")
    private final String name;
    @Size(max = 200, message = "Описание максимум 200 символов")
    private final String description;
    @PastOrPresent(message = "Дата не может быть в будущем")
    private final LocalDate releaseDate;
    @Positive(message = "Дата должна быть положительной")
    private final int duration;

}
