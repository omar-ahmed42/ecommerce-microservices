package com.omarahmed42.order.message.payload;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarahmed42.order.message.payload.item.Item;

import lombok.Data;

@Data
public class ReserveStockPayload implements Serializable {
    private Long orderId;
    private List<Item> items = new ArrayList<>();
    private String reason = "Order Fulfillment";
    private String correlationId;

    public Map<String, String> asMap(ObjectMapper mapper) throws JsonProcessingException {
        Map<String, String> result = new HashMap<>();
        result.put("orderId", String.valueOf(orderId));
        result.put("items", mapper.writeValueAsString(items));
        result.put("reason", reason);
        result.put("correlationId", correlationId);
        return result;
    }

    // public static ReserveStockPayload fromMap(Map<String, String> values) {
    // ReserveStockPayload payload = new ReserveStockPayload();
    // payload.setOrderId(Long.parseLong(values.get("orderId")));
    // payload.setReason(values.get("reason"));
    // payload.setI
    // return payload;
    // }

    public void addItem(Item item) {
        this.items.add(item);
    }

    public void addItems(Collection<Item> items) {
        this.items.addAll(items);
    }
}
