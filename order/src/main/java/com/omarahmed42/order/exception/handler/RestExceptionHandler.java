package com.omarahmed42.order.exception.handler;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.omarahmed42.order.exception.generic.BadRequestException;
import com.omarahmed42.order.exception.generic.ForbiddenException;
import com.omarahmed42.order.exception.generic.InternalServerErrorException;
import com.omarahmed42.order.exception.generic.NotFoundException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException e) {
        logError(e);
        return ResponseEntity.status(NotFoundException.STATUS_CODE).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(BadRequestException e) {
        logError(e);
        return ResponseEntity.status(BadRequestException.STATUS_CODE).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<?> handleInternalServerErrorException(InternalServerErrorException e) {
        logError(e);
        return ResponseEntity.status(InternalServerErrorException.STATUS_CODE).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> handleForbiddenException(ForbiddenException e) {
        logError(e);
        return ResponseEntity.status(ForbiddenException.STATUS_CODE).body(Map.of("message", e.getMessage()));
    }

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