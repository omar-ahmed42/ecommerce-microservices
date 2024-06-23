package com.omarahmed42.order.message.payload;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.Data;

@Data
public class RetrievePaymentPayload implements Serializable {
    private Long orderId;
    private BigDecimal totalCost;
    private Long paymentId;
    private String userId;
    private String reason = "Order Fulfillment";
    private String correlationId;

    public Map<String, String> asMap() throws JsonProcessingException {
        Map<String, String> result = new HashMap<>();
        result.put("orderId", String.valueOf(orderId));
        result.put("paymentId", String.valueOf(paymentId));
        result.put("userId", String.valueOf(userId));
        result.put("reason", reason);
        result.put("correlationId", correlationId);
        return result;
    }
}
