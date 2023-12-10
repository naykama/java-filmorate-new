package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {
    private int id;
    @NotEmpty(message = "Электронная почта не может быть")
    @Email(message = "Электронная символ @")
    private final String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин содержать пробелы")
    private final String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    private final LocalDate birthday;
    private Set<Long> friends;

}
