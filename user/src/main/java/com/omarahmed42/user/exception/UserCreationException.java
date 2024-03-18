package com.omarahmed42.user.exception;

public class UserCreationException extends InternalServerException {

    public UserCreationException(String message) {
        super(message);
    }

    public UserCreationException() {
        super("User Creation " + DEFAULT_MESSAGE);
    }

}
