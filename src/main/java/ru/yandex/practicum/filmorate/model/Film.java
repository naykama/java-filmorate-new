package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;


import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @NotEmpty(message = "Имя не содержит символов")
    private final String name;
    @Size(max = 200, message = "Описание максимум 200 символов")
    private final String description;
    @DataValidAnnotation(message = "Дата релиза — не раньше 28 декабря 1895 года")
    private final LocalDate releaseDate;
    @Positive(message = "Дата должна быть положительной")
    private final int duration;
    private int like;




    public Film(int id, String name, String description, LocalDate releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.like = 0;

    }
}
