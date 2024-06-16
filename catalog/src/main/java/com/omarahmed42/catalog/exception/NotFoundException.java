package com.omarahmed42.catalog.exception;

public class NotFoundException extends RuntimeException {

    public static final Integer STATUS_CODE = 404;
    protected static final String DEFAULT_MESSAGE = "Not Found";

    public NotFoundException(String msg) {
        super(msg);
    }

    public NotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    protected static String createMessage(String resourceName) {
        return resourceName + " " + NotFoundException.DEFAULT_MESSAGE;
    }
}
