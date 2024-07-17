package com.omarahmed42.order.exception;

import com.omarahmed42.order.exception.generic.ForbiddenException;

public class ForbiddenOrderAccessException extends ForbiddenException {
    public ForbiddenOrderAccessException(String message) {
        super(message);
    }

    public ForbiddenOrderAccessException() {
        super("Forbidden Order Access");
    }
}
