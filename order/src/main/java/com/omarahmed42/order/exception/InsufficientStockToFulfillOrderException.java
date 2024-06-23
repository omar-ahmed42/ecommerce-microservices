package com.omarahmed42.order.exception;

public class InsufficientStockToFulfillOrderException extends InsufficientStockException {

    public InsufficientStockToFulfillOrderException(String message) {
        super(message);
    }

}
