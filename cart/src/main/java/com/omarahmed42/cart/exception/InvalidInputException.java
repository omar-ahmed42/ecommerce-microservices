package com.omarahmed42.cart.exception;

import com.omarahmed42.cart.exception.generic.BadRequestException;

public class InvalidInputException extends BadRequestException {
    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException() {
        super("Invalid input");
    }
}
