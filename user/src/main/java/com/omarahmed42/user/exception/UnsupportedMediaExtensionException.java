package com.omarahmed42.user.exception;

public class UnsupportedMediaExtensionException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Unsupported media";

    public UnsupportedMediaExtensionException() {
        super(DEFAULT_MESSAGE);
    }

    public UnsupportedMediaExtensionException(String message) {
        super(message);
    }
}