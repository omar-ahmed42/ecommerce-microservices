package com.omarahmed42.order.exception.generic;

public class ForbiddenException extends RuntimeException {
    
    public static final Integer STATUS_CODE = 403;
    protected static final String DEFAULT_MESSAGE = "Forbidden";

    public ForbiddenException(String msg) {
        super(msg);
    }

    public ForbiddenException() {
        super(DEFAULT_MESSAGE);
    }
}
