package ru.yandex.practicum.filmorate.exeption;

public class UserFoundException extends RuntimeException {
    public UserFoundException(String s) {
        super(s);
    }
}
