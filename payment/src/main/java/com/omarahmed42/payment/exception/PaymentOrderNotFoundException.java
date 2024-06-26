package com.omarahmed42.payment.exception;

public class PaymentOrderNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Payment order not found";

    public PaymentOrderNotFoundException(String msg) {
        super(msg);
    }

    public PaymentOrderNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
