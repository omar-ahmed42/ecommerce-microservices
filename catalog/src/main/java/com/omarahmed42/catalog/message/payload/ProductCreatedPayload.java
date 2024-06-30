package com.omarahmed42.catalog.message.payload;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class ProductCreatedPayload implements Serializable {
    private Long productId;
    private Integer stock;
    private String reason = "Product created";
    private String correlationId;

    public Map<String, String> asMap() {
        Map<String, String> result = new HashMap<>();
        result.put("productId", String.valueOf(productId));
        result.put("stock", String.valueOf(stock));
        result.put("reason", reason);
        result.put("correlationId", correlationId);
        return result;
    }
}
