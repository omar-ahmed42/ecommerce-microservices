package com.omarahmed42.payment.message.payload;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.Data;

@Data
public class PaymentFailedPayload implements Serializable {
    private Long orderId;
    private String paymentIntentId;
    private String reason = "Order Fulfillment";
    private String correlationId;

    public Map<String, String> asMap() throws JsonProcessingException {
        Map<String, String> result = new HashMap<>();
        result.put("orderId", String.valueOf(orderId));
        result.put("paymentIntentId", paymentIntentId);
        result.put("reason", reason);
        result.put("correlationId", correlationId);
        return result;
    }
}
