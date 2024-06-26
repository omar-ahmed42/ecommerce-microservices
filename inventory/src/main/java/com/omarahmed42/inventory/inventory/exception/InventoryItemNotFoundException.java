package com.omarahmed42.inventory.inventory.exception;

public class InventoryItemNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Inventory item not found";

    public InventoryItemNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public InventoryItemNotFoundException(String msg) {
        super(msg);
    }

}
