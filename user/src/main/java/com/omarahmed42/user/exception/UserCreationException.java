package com.omarahmed42.user.exception;

public class UserCreationException extends InternalServerException {

    private Integer statusCode = null;

    public UserCreationException(String message) {
        super(message);
    }

    public UserCreationException(Integer statusCode, String errorMessage) {
        super(errorMessage);
        this.statusCode = statusCode;
    }

    public UserCreationException() {
        super("User Creation " + DEFAULT_MESSAGE);
    }

    public Integer getStatusCode() {
        return this.statusCode;
    }
}
