package com.omarahmed42.catalog.exception;

public class ForbiddenProductAccessException extends ForbiddenException {
    private static final String DEFAULT_MESSAGE = "Forbidden: Cannot access post";

    public ForbiddenProductAccessException() {
        super(DEFAULT_MESSAGE);
    }

    public ForbiddenProductAccessException(String message) {
        super(message);
    }
}