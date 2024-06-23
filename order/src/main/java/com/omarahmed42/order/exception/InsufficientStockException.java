package com.omarahmed42.order.exception;

import com.omarahmed42.order.exception.generic.BadRequestException;

public class InsufficientStockException extends BadRequestException {

    public InsufficientStockException(String message) {
        super(message);
    }

}
