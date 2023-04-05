package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.constants.DatePattern;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class ExceptionController {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DatePattern.DATE_FORMAT);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiError notFoundExceptionHandler(final NotFoundException e) {
        return new ApiError(e.getMessage(), "The object you are looking for does not exist.",
                HttpStatus.NOT_FOUND.getReasonPhrase().toUpperCase(), LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError requestExceptionHandler(final RequestException e) {
        return new ApiError(e.getMessage(), "Bad request", HttpStatus.BAD_REQUEST.getReasonPhrase().toUpperCase(),
                LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError timeExceptionHandler(final TimeException e) {
        return new ApiError(e.getMessage(), "Time exception", HttpStatus.CONFLICT.getReasonPhrase().toUpperCase(),
                LocalDateTime.now().format(dateFormatter));
    }
}
