package ru.yandex.practicum.filmorate.model;


import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class User {
    private int id;
    @NotEmpty(message = "Электронная почта не может быть")
    @Email(message = "Электронная символ @")
    private final String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин содержать пробелы")
    private final String login;
    private String name;
    @NotNull
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private final LocalDate birthday;
    private Set<Integer> friends;
    private List<Film> filmsLike;


    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = new HashSet<>();
        this.filmsLike = new ArrayList<>();
    }

}
