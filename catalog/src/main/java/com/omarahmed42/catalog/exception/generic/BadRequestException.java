package com.omarahmed42.catalog.exception.generic;

public class BadRequestException extends RuntimeException {

    public static final Integer STATUS_CODE = 400;
    protected static final String DEFAULT_MESSAGE = "Bad request";

    public BadRequestException(String msg) {
        super(msg);
    }

    public BadRequestException() {
        super(DEFAULT_MESSAGE);
    }
}
