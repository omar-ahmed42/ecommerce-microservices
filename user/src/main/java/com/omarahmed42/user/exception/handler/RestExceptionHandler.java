package com.omarahmed42.user.exception.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.omarahmed42.user.exception.InternalServerException;
import com.omarahmed42.user.exception.NotFoundException;
import com.omarahmed42.user.exception.UserCreationException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path.Node;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler {

    record ErrorResponse(String errorMessage) {
    }

    record ValidationErrorResponse(String fieldName, String errorMessage) {
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationErrorResponse>> handleMethodArgumentValidationExceptions(
            MethodArgumentNotValidException ex) {
        logError(ex);
        List<ValidationErrorResponse> errors = new ArrayList<>(ex.getBindingResult().getErrorCount());
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = ((FieldError) error).getDefaultMessage();
            errors.add(new ValidationErrorResponse(fieldName, errorMessage));
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<List<ValidationErrorResponse>> handleConstraintViolationException(
            jakarta.validation.ConstraintViolationException ex) {
        logError(ex);
        List<ValidationErrorResponse> errors = new ArrayList<>();
        ex.getConstraintViolations().forEach(error -> {
            errors.add(new ValidationErrorResponse(extractPropertyName(error), error.getMessage()));
        });
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * @example
     * 
     *          <pre>
     * method -> register(User user)
     * property: register.user.* where * indicates all the properties for the user object (example for a property firstName)
     * register.user.firstName
     * iterator.next() -> register.user.firstName
     * iterator.next() -> user.firstName
     * iterator.next().getName() -> "firstName"
     *          </pre>
     */
    private String extractPropertyName(ConstraintViolation<?> constraintViolation) {
        Iterator<Node> iterator = constraintViolation.getPropertyPath().iterator();
        iterator.next(); // Skip method name
        iterator.next(); // Skip object name
        return iterator.next().getName();
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException ex) {
        logError(ex);
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            NotFoundException ex) {
        logError(ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(UserCreationException.class)
    public ResponseEntity<ErrorResponse> handleUserCreationException(UserCreationException ex) {
        logError(ex);

        Integer statusCode = ex.getStatusCode();
        if (statusCode == null)
            return ResponseEntity.internalServerError().body(new ErrorResponse(ex.getMessage()));

        String errorMessage = ex.getMessage();
        return ResponseEntity.status(statusCode).body(new ErrorResponse(errorMessage));
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerException(InternalServerException ex) {
        logError(ex);
        return ResponseEntity.internalServerError().body(new ErrorResponse(ex.getMessage()));
    }

    private void logError(Exception e) {
        log.error("{} || {}", e.getMessage(), e);
    }

}
