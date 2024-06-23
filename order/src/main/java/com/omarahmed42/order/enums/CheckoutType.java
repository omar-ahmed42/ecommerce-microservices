package com.omarahmed42.order.enums;

public enum CheckoutType {
    DIRECT_PURCHASE("direct_purchase"),
    CART_PURCHASE("cart_purchase");

    private final String type;

    private CheckoutType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type;
    }

    public String type() {
        return this.type;
    }

}
