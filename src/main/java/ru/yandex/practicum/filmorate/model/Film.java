package ru.yandex.practicum.filmorate.model;


import lombok.Data;


import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Objects;

@Data
public class Film {
    private int id;
    @NotEmpty(message = "Имя не содержит символов")
    private final String name;
    @Size(max = 200, message = "Описание максимум 200 символов")
    @NotNull
    private final String description;
    @DataValidAnnotation(message = "Дата релиза — не раньше 28 декабря 1895 года")
    private final LocalDate releaseDate;
    @Positive(message = "Продолжительность должна быть положительной")
    private final int duration;
    private int rate;
    private LinkedHashSet<Genre> genres;
    @NotNull
    private Mpa mpa;

    public Film(int id, String name, String description, LocalDate releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = 0;
        this.genres = new LinkedHashSet<>();
        this.mpa = new Mpa();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Film)) return false;
        Film film = (Film) o;
        return id == film.id && duration == film.duration && rate == film.rate && Objects.equals(name, film.name) && Objects.equals(description, film.description) && Objects.equals(releaseDate, film.releaseDate) && Objects.equals(genres, film.genres) && Objects.equals(mpa, film.mpa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, releaseDate, duration, rate, genres, mpa);
    }
}
