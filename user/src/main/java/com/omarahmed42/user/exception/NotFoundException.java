package com.omarahmed42.user.exception;

public class NotFoundException extends RuntimeException {
    
    public static final int STATUS_CODE = 404;
    public static final String DEFAULT_MESSAGE = "Not Found";

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException() {
        super(DEFAULT_MESSAGE);
    }
    
}
