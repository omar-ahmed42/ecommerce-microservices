package com.omarahmed42.cart.exception;

import com.omarahmed42.cart.exception.generic.NotFoundException;

public class CartItemNotFoundException extends NotFoundException {
    private static final String RESOURCE_NAME = "Cart Item";

    public CartItemNotFoundException(String message) {
        super(message);
    }

    public CartItemNotFoundException() {
        super(createMessage(RESOURCE_NAME));
    }
}
