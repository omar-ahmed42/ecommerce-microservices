package com.omarahmed42.order.exception;

import com.omarahmed42.order.exception.generic.NotFoundException;

public class OrderNotFoundException extends NotFoundException {
    private static final String DEFAULT_MESSAGE = "Order not found";

    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
