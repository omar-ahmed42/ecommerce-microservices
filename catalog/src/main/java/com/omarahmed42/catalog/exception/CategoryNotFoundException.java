package com.omarahmed42.catalog.exception;

import com.omarahmed42.catalog.exception.generic.NotFoundException;

public class CategoryNotFoundException extends NotFoundException {
    private static final String RESOURCE_NAME = "Category";

    public CategoryNotFoundException(String message) {
        super(message);
    }

    public CategoryNotFoundException() {
        super(createMessage(RESOURCE_NAME));
    }
}
