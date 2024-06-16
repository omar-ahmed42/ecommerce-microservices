package com.omarahmed42.catalog.exception;

import com.omarahmed42.catalog.exception.generic.BadRequestException;

public class InvalidInputException extends BadRequestException {
    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException() {
        super("Invalid input");
    }
}
