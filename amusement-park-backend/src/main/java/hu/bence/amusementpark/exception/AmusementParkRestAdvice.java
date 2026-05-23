package hu.bence.amusementpark.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Stream;

import static hu.bence.amusementpark.constants.ErrorMessageConstants.*;

@Slf4j
@RestControllerAdvice
public class AmusementParkRestAdvice {

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Throwable throwable) {
        log.error(ERROR, throwable);
        return UNEXPECTED_ERROR_OCCURRED;
    }

    @ExceptionHandler(AmusementParkException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleAmusementParkException(AmusementParkException amusementParkException) {
        log.error(ERROR, amusementParkException);
        return amusementParkException.getMessage();
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadCredentialsException(BadCredentialsException badCredentialsException) {
        log.error(ERROR, badCredentialsException);
        return badCredentialsException.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMethodArgumentNotValidException(
            MethodArgumentNotValidException methodArgumentNotValidException) {
        log.error(ERROR, methodArgumentNotValidException);
        BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
        return Stream
                .concat(bindingResult.getFieldErrors().stream().map(this::convertToMessage),
                        bindingResult.getGlobalErrors().stream().map(this::convertToMessage))
                .reduce(String::concat).orElse(COULD_NOT_GET_VALIDATION_MESSAGE);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleConstraintViolationException(ConstraintViolationException constraintViolationException) {
        log.error(ERROR, constraintViolationException);
        return constraintViolationException.getMessage();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleHttpMessageNotReadableException(
            HttpMessageNotReadableException httpMessageNotReadableException) {
        log.error(ERROR, httpMessageNotReadableException);
        return "No input.";
    }

    private String convertToMessage(FieldError fieldError) {
        return validationError(fieldError.getField(), fieldError.getDefaultMessage());
    }

    private String convertToMessage(ObjectError objectError) {
        return validationError(objectError.getDefaultMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(AccessDeniedException accessDeniedException) {
        log.error(ERROR, accessDeniedException);
        return accessDeniedException.getMessage();
    }
}
