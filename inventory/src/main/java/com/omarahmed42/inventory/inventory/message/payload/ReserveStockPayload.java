package com.omarahmed42.inventory.inventory.message.payload;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarahmed42.inventory.inventory.dto.message.InventoryItem;

import lombok.Data;

@Data
public class ReserveStockPayload implements Serializable {
    private Long orderId;
    private List<InventoryItem> items = new ArrayList<>();
    private String reason = "Reserve stock";
    private String correlationId;

    public Map<String, String> asMap(ObjectMapper mapper) throws JsonProcessingException {
        Map<String, String> result = new HashMap<>();
        result.put("orderId", String.valueOf(orderId));
        result.put("items", mapper.writeValueAsString(items));
        result.put("reason", reason);
        result.put("correlationId", correlationId);
        return result;
    }

    public void addItem(InventoryItem item) {
        this.items.add(item);
    }

    public void addItems(Collection<InventoryItem> items) {
        this.items.addAll(items);
    }
}
