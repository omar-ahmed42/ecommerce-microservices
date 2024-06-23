package com.omarahmed42.order.dto.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.Data;

@Data
public class FlowMessage<T> implements Serializable {
    private T payload;
    private Map<String, String> metadata = new HashMap<>();
    private String correlationId = UUID.randomUUID().toString();

    public FlowMessage(T payload) {
        this.payload = payload;
    }

    public FlowMessage(T payload, String correlationId) {
        this.payload = payload;
        this.correlationId = correlationId;
    }

    @Override
    public String toString() {
        return "FlowMessage [payload=" + payload + ", metadata=" + metadata + ", correlationId=" + correlationId + "]";
    }
}
