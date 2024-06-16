package com.omarahmed42.catalog.exception;

public class CategoryNotFoundException extends NotFoundException {
    private static final String RESOURCE_NAME = "Category";

    public CategoryNotFoundException(String message) {
        super(message);
    }

    public CategoryNotFoundException() {
        super(createMessage(RESOURCE_NAME));
    }
}
