package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class Genre {
    private int id;
    private String name;

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genre)) return false;
        Genre genre = (Genre) o;
        return id == genre.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
