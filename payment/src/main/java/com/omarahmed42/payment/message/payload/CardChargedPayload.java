package com.omarahmed42.payment.message.payload;

import java.util.Map;
import java.util.HashMap;

import lombok.Data;

@Data
public class CardChargedPayload {
    private String paymentOrderId;
    private String paymentIntentId;
    private Long orderId;
    private String paymentStatus;
    private String reason = "Order Fulfillment";
    private String correlationId;

    public Map<String, String> asMap() {
        Map<String, String> result = new HashMap<>();
        result.put("paymentOrderId", paymentOrderId);
        result.put("paymentIntentId", paymentIntentId);
        result.put("orderId", String.valueOf(orderId));
        result.put("paymentStatus", paymentStatus);
        result.put("reason", reason);
        result.put("correlationId", correlationId);
        return result;
    }

    public static CardChargedPayload fromMap(Map<String, Object> map) {
        CardChargedPayload payload = new CardChargedPayload();
        payload.setOrderId(Long.valueOf((String) map.get("orderId")));
        payload.setPaymentOrderId((String) map.get("paymentOrderId"));
        payload.setPaymentIntentId((String) map.get("paymentIntentId"));
        payload.setReason((String) map.get("reason"));
        payload.setPaymentStatus((String) map.get("paymentStatus"));
        payload.setCorrelationId((String) map.get("correlationId"));
        return payload;
    }
}
