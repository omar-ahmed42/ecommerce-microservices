package com.omarahmed42.inventory.inventory.exception;

public class InsufficientStockException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Item is currently out of stock";

    public InsufficientStockException() {
        super(DEFAULT_MESSAGE);
    }

    public InsufficientStockException(String msg) {
        super(msg);
    }
}
