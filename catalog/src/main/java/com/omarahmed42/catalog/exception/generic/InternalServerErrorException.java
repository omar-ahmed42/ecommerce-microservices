package com.omarahmed42.catalog.exception.generic;

public class InternalServerErrorException extends RuntimeException {

    public static final Integer STATUS_CODE = 500;
    protected static final String DEFAULT_MESSAGE = "Internal Server Error";

    public InternalServerErrorException(String msg) {
        super(msg);
    }

    public InternalServerErrorException() {
        super(DEFAULT_MESSAGE);
    }
}
