package com.omarahmed42.payment.enums;

public enum PaymentGatewayType {
    STRIPE("Stripe", 1),
    PAYPAL("Paypal", 2);

    private final String type;
    private final int value;

    private PaymentGatewayType(String type, int value) {
        this.type = type;
        this.value = value;
    }

    public String type() {
        return this.type;
    }

    public int value() {
        return this.value;
    }

    @Override
    public String toString() {
        return type();
    }
}
