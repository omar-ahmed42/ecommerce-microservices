package com.omarahmed42.catalog.exception;

import com.omarahmed42.catalog.exception.generic.NotFoundException;

public class ProductNotFoundException extends NotFoundException {
    private static final String RESOURCE_NAME = "Product";

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(Long id) {
        super("Product with id " + id + " not found");
    }

    public ProductNotFoundException() {
        super(createMessage(RESOURCE_NAME));
    }
}
