package com.omarahmed42.payment.message.payload;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class PaymentRetrievedPayload implements Serializable {
    private Long orderId;
    private String reason = "Order Fulfillment";
    private String correlationId;

    public Map<String, String> asMap() {
        Map<String, String> result = new HashMap<>();
        result.put("orderId", String.valueOf(orderId));
        result.put("reason", reason);
        result.put("correlationId", correlationId);
        return result;
    }

    public static PaymentRetrievedPayload fromMap(Map<String, Object> map) {
        PaymentRetrievedPayload payload = new PaymentRetrievedPayload();
        payload.setOrderId(Long.valueOf((String) map.get("orderId")));
        payload.setReason((String) map.get("reason"));
        payload.setCorrelationId((String) map.get("correlationId"));
        return payload;
    }
}
