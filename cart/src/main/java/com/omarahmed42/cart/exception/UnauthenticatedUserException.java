package com.omarahmed42.cart.exception;

public class UnauthenticatedUserException extends RuntimeException {
    public UnauthenticatedUserException() {
        super("Unauthenticated user");
    }
}
