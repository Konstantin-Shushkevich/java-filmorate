package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final RuntimeException e) {
        log.error("Exception in validation process");
        return new ErrorResponse("Failed validation", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.error("NotFoundException was thrown");
        return new ErrorResponse("Search was failed", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInternalServerException(final InternalServerException e) {
        log.error("InternalServerException was thrown");
        return new ErrorResponse("Data needs to be verified", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOtherExceptions(final Exception e) {
        log.error("Exception was thrown");
        return new ErrorResponse("Something went wrong", e.getMessage());
    }
}
