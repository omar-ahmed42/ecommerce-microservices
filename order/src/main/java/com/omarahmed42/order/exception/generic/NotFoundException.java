package com.omarahmed42.order.exception.generic;

public class NotFoundException extends RuntimeException {

    public static final Integer STATUS_CODE = 404;
    protected static final String DEFAULT_MESSAGE = "Not Found";

    public NotFoundException(String msg) {
        super(msg);
    }

    public NotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
