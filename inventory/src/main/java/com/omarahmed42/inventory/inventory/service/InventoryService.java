package com.omarahmed42.inventory.inventory.service;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.omarahmed42.inventory.inventory.dto.message.InventoryItem;
import com.omarahmed42.inventory.inventory.dto.request.InventoryRequest;
import com.omarahmed42.inventory.inventory.dto.response.InventoryResponse;

public interface InventoryService {
    void reserveInventory(List<InventoryItem> items) throws JsonProcessingException;

    InventoryResponse reserveInventory(InventoryRequest inventoryRequest);

    InventoryResponse updateInventory(Long id, InventoryRequest inventoryRequest);

    void deleteInventory(Long productId);

    InventoryResponse getInventory(Long productId);
}
