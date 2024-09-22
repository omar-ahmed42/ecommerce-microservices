package com.omarahmed42.catalog.enums;

public enum MediaVariant {
    MAIN("MAIN", 0),
    PT01("PT01", 1),
    PT02("PT02", 2),
    PT03("PT03", 3),
    PT04("PT04", 4),
    PT05("PT05", 5),
    PT06("PT06", 6),
    PT07("PT07", 7),
    PT08("PT08", 8);

    private final String text;
    private final int value;

    MediaVariant(final String text, final int value) {
        this.text = text;
        this.value = value;
    }

    @Override
    public String toString() {
        return text;
    }

    public String text() {
        return this.text;
    }

    public int value() {
        return this.value;
    }
}
