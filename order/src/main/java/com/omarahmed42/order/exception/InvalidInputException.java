package com.omarahmed42.order.exception;

import com.omarahmed42.order.exception.generic.BadRequestException;

public class InvalidInputException extends BadRequestException {
    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException() {
        super("Invalid input");
    }
}
