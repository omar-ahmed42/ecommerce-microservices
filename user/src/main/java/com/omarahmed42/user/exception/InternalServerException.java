package com.omarahmed42.user.exception;

public class InternalServerException extends RuntimeException {
      
    public static final int STATUS_CODE = 500;
    public static final String DEFAULT_MESSAGE = "Internal Server Error";

    public InternalServerException(String message) {
        super(message);
    }

    public InternalServerException() {
        super(DEFAULT_MESSAGE);
    }
}
