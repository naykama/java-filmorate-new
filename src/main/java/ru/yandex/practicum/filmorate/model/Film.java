package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;


import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Film {
    private int id;
    @NotEmpty(message = "Имя не содержит символов")
    private final String name;
    @Size(max = 200, message = "Описание максимум 200 символов")
    private final String description;
    @PastOrPresent(message = "Дата не может быть в будущем")
    private final LocalDate releaseDate;
    @Positive(message = "Дата должна быть положительной")
    private final int duration;

}
