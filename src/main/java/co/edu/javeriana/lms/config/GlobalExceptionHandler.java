package co.edu.javeriana.lms.config;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;


import co.edu.javeriana.lms.dtos.ErrorDto;
import co.edu.javeriana.lms.dtos.ValidationErrorDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<ValidationErrorDto> errors = ex.getBindingResult().getAllErrors().stream().map(error -> {
            String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
            String message = error.getDefaultMessage();
            return new ValidationErrorDto(fieldName, message);
        }).collect(Collectors.toList());

        log.info("Validation failed: {}", errors);

        return new ResponseEntity<>(new ErrorDto("Validation failed", errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> handleBindException(BindException ex) {
        List<ValidationErrorDto> errors = ex.getBindingResult().getAllErrors().stream().map(error -> {
            String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
            String message = error.getDefaultMessage();
            return new ValidationErrorDto(fieldName, message);
        }).collect(Collectors.toList());

        log.info("Validation failed: {}", errors);

        return new ResponseEntity<>(new ErrorDto("Validation failed", errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Object> handleWebExchangeBindException(WebExchangeBindException ex) {
        List<ValidationErrorDto> errors = ex.getBindingResult().getAllErrors().stream().map(error -> {
            String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
            String message = error.getDefaultMessage();
            return new ValidationErrorDto(fieldName, message);
        }).collect(Collectors.toList());

        log.info("Validation failed: {}", errors);

        return new ResponseEntity<>(new ErrorDto("Validation failed", errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Business validation failed: {}", ex.getMessage());
        ErrorDto errorDto = new ErrorDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("Invalid request structure: {}", ex.getMessage());

        return new ResponseEntity<>(new ErrorDto("Request body error", "Invalid request structure: " + ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());

        return new ResponseEntity<>(new ErrorDto("Entity not found", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.warn("Invalid data or duplicate entry: {}", ex.getMostSpecificCause().getMessage());

        return new ResponseEntity<>(new ErrorDto("Invalid data or duplicate entry", ex.getMostSpecificCause().getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Validation failed: {}", ex.getMessage());

        return new ResponseEntity<>(new ErrorDto("Validation failed", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}