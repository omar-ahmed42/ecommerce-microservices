package com.omarahmed42.catalog.enums;

public enum AttachmentStatus {
    UPLOADING("UPLOADING", 1),
    COMPLETED("COMPLETED", 2),
    FAILED("FAILED", 3);

    private final String text;
    private final int value;

    AttachmentStatus(final String text, final int value) {
        this.text = text;
        this.value = value;
    }

    @Override
    public String toString() {
        return text;
    }

    public int getValue() {
        return value;
    }
}