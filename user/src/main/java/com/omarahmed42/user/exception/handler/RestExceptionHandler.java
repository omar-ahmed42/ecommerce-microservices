package com.omarahmed42.user.exception.handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path.Node;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentValidationExceptions(
            MethodArgumentNotValidException ex) {
        log.error(ex.getMessage(), ex);
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = ((FieldError) error).getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(
            jakarta.validation.ConstraintViolationException ex) {
        log.error(ex.getMessage(), ex);
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(error -> {
            errors.put(extractPropertyName(error), error.getMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

    /**
    * @example 
    * <pre>
    * method -> register(User user)
    * property: register.user.* where * indicates all the properties for the user object (example for a property firstName)
    * register.user.firstName
    * iterator.next() -> register.user.firstName
    * iterator.next() -> user.firstName
    * iterator.next().getName() -> "firstName"
    * </pre>
    */
    private String extractPropertyName(ConstraintViolation<?> constraintViolation) {
        Iterator<Node> iterator = constraintViolation.getPropertyPath().iterator();
        iterator.next(); // Skip method name
        iterator.next(); // Skip object name
        return iterator.next().getName();
    }
}
