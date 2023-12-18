package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exeption.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler()
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundException(final EntityNotFoundException e) {
        log.debug("Искомый объект не найден 404 Not found {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validException(final IllegalArgumentException e) {
        log.debug("Ошибка валидации 400 Bad request {}", e.getMessage());
        return new ErrorResponse("Ошибка валидации");
    }
    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validCountException(final MethodArgumentNotValidException e) {
        log.debug("Ошибка валидации 400 Bad request {}", e.getMessage());
        return new ErrorResponse("Ошибка валидации");
    }


    @ExceptionHandler()
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.debug("Произошла непредвиденная ошибка {}", e.getMessage());
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }

}
