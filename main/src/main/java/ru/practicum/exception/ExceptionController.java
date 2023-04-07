package ru.practicum.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.constants.DatePattern;

import javax.validation.ValidationException;
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
                HttpStatus.NOT_FOUND.getReasonPhrase(), LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError requestExceptionHandler(final RequestException e) {
        return new ApiError(e.getMessage(), "Request exception", HttpStatus.CONFLICT.getReasonPhrase(),
                LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError timeExceptionHandler(final TimeException e) {
        return new ApiError(e.getMessage(), "Time exception", HttpStatus.CONFLICT.getReasonPhrase(),
                LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiError emailDuplicatedException(final ValidationException e) {
        return new ApiError(e.getMessage(), "Validation exception", HttpStatus.CONFLICT.getReasonPhrase(),
                LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError sqlExceptionHandler(final DataIntegrityViolationException e) {
        return new ApiError(e.getMessage(), "Unique value error", HttpStatus.CONFLICT.getReasonPhrase(),
                LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError eventExceptionHandler(final EventException e) {
        return new ApiError(e.getMessage(), "Event exception", HttpStatus.CREATED.getReasonPhrase(),
                LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleHttpMessageNotReadableException(final HttpMessageNotReadableException e) {
        return new ApiError(e.getMessage(), "The required data was not sent in the request",
                HttpStatus.CREATED.getReasonPhrase(), LocalDateTime.now().format(dateFormatter));
    }
}
