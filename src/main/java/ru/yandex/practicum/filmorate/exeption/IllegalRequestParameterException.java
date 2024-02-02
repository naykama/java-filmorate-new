package ru.yandex.practicum.filmorate.exeption;

public class IllegalRequestParameterException extends RuntimeException {
    public IllegalRequestParameterException(String message) {
        super(message);
    }
}
