package com.omarahmed42.order.message.payload;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarahmed42.order.message.payload.item.PricedItem;

import lombok.Data;

@Data
public class CalculateOrderCostPayload implements Serializable {
    private Long orderId;
    private List<PricedItem> items = new ArrayList<>();
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

    public static CalculateOrderCostPayload fromMap(Map<String, ?> map, ObjectMapper mapper)
            throws JsonProcessingException {
        CalculateOrderCostPayload payload = new CalculateOrderCostPayload();
        payload.setOrderId(Long.valueOf((String) map.get("orderId")));
        payload.setItems(mapper.readValue((String) map.get("items"), new TypeReference<List<PricedItem>>() {
        }));
        payload.setReason((String) map.get("reason"));
        payload.setCorrelationId((String) map.get("correlationId"));

        return payload;
    }

    public void addPricedItem(PricedItem item) {
        this.items.add(item);
    }

    public void addPricedItems(Collection<PricedItem> items) {
        this.items.addAll(items);
    }
}
