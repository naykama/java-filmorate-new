package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class Director {
    private int id;
    @NotBlank (message = "Имя не содержит символов")
    @Size(max = 100, message = "Имя режиссера содержит больше 100 символов")
    private String name;
}
