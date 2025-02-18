package co.edu.javeriana.lms.config;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import co.edu.javeriana.lms.dtos.ErrorDto;
import co.edu.javeriana.lms.dtos.ValidationErrorDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, WebExchangeBindException.class})
    public ResponseEntity<Object> handleMethodArgumentNotValidException(Exception e) {
        List<ValidationErrorDto> errors;
        
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            errors = ex.getBindingResult().getAllErrors().stream().map(error -> {
                String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
                String message = error.getDefaultMessage();
                return new ValidationErrorDto(fieldName, message);
            }).collect(Collectors.toList());
        } else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            errors = ex.getBindingResult().getAllErrors().stream().map(error -> {
                String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
                String message = error.getDefaultMessage();
                return new ValidationErrorDto(fieldName, message);
            }).collect(Collectors.toList());
        } else if (e instanceof WebExchangeBindException) {
            WebExchangeBindException ex = (WebExchangeBindException) e;
            errors = ex.getBindingResult().getAllErrors().stream().map(error -> {
                String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
                String message = error.getDefaultMessage();
                return new ValidationErrorDto(fieldName, message);
            }).collect(Collectors.toList());
        } else {
            // Default case, shouldn't happen
            log.error("Unknown exception type: {}", e.getClass().getName());
            errors = List.of(new ValidationErrorDto("unknown", "Unknown validation error"));
        }

        log.info("Validation failed: {}", errors);

        return new ResponseEntity<>(new ErrorDto("Validation failed", errors), HttpStatus.BAD_REQUEST);
    }
}