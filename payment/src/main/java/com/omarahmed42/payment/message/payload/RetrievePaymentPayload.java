package com.omarahmed42.payment.message.payload;

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
    private String paymentId;
    private String userId;
    private String reason = "Order Fulfillment";
    private String correlationId;

    public Map<String, String> asMap() throws JsonProcessingException {
        Map<String, String> result = new HashMap<>();
        result.put("orderId", String.valueOf(orderId));
        result.put("totalCost", totalCost.toString());
        result.put("paymentId", paymentId);
        result.put("userId", userId);
        result.put("reason", reason);
        result.put("correlationId", correlationId);
        return result;
    }
    
    public static RetrievePaymentPayload fromMap(Map<String, Object> map) {
        RetrievePaymentPayload payload = new RetrievePaymentPayload();
        payload.setOrderId(Long.valueOf((String) map.get("orderId")));
        payload.setTotalCost(new BigDecimal((String) map.get("totalCost")));
        payload.setPaymentId((String) map.get("paymentId"));
        payload.setUserId((String) map.get("userId"));
        payload.setReason((String) map.get("reason"));
        payload.setCorrelationId((String) map.get("correlationId"));
        return payload;
    }
}
