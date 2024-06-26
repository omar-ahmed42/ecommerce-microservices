package com.omarahmed42.inventory.inventory.exception;

public class QuantityExceedsStockException extends InsufficientStockException {
    private static final String DEFAULT_MESSAGE = "Quantity requested exceeds the stock quantity.";

    public QuantityExceedsStockException() {
        super(DEFAULT_MESSAGE);
    }

    public QuantityExceedsStockException(String msg) {
        super(msg);
    }
}
