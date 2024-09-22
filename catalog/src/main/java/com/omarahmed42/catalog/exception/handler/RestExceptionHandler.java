package com.omarahmed42.catalog.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Void> handleRuntimeException(RuntimeException runtimeException) {
        logError(runtimeException);
        return ResponseEntity.status(422).build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleException(Exception e) {
        logError(e);
        return ResponseEntity.internalServerError().build();
    }

    private void logError(Exception e) {
        log.error(e.getMessage(), e);
    }
}