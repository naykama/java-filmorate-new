package ru.yandex.practicum.filmorate.exeption;

public class IllegalRequestParameter extends RuntimeException {
    public IllegalRequestParameter(String s) {
        super(s);
    }

}
