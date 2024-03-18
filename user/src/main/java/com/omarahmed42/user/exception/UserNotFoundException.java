package com.omarahmed42.user.exception;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException() {
        super("User " + DEFAULT_MESSAGE);
    }

}
