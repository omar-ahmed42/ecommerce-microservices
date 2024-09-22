package com.omarahmed42.catalog.exception;

import com.omarahmed42.catalog.exception.generic.BadRequestException;

public class MaxProductMediaReachedException extends BadRequestException {
    protected static final String DEFAULT_MESSAGE = "Max product media reached";

    public MaxProductMediaReachedException(String msg) {
        super(msg);
    }

    public MaxProductMediaReachedException() {
        super(DEFAULT_MESSAGE);
    }
}
