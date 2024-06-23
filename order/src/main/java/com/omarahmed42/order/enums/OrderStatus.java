package com.omarahmed42.order.enums;

public enum OrderStatus {
    FULFILLED("FULFILLED", 1),
    CANCELLED("CANCELLED", 2),
    REJECTED("REJECTED", 3),
    SHIPPING("SHIPPING", 4),
    PENDING("PENDING", 5);

    private final String text;
    private final int value;

    private OrderStatus(String text, int value) {
        this.text = text;
        this.value = value;
    }

    @Override
    public String toString() {
        return this.text;
    }

    public int value() {
        return this.value;
    }
}
