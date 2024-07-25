package com.omarahmed42.inventory.inventory.service;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.omarahmed42.inventory.inventory.dto.message.InventoryItem;
import com.omarahmed42.inventory.inventory.dto.request.InventoryRequest;
import com.omarahmed42.inventory.inventory.dto.response.InventoryResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface InventoryService {
    InventoryResponse addInventoryItem(InventoryRequest inventoryRequest);

    void reserveInventory(List<InventoryItem> items) throws JsonProcessingException;

    InventoryResponse reserveInventory(@NotNull @Valid InventoryRequest inventoryRequest);

    InventoryResponse updateInventory(@NotNull Long id, @NotNull @Valid InventoryRequest inventoryRequest);

    void deleteInventory(@NotNull(message = "Product ID cannot be empty") Long productId);

    InventoryResponse getInventory(@NotNull(message = "Product ID cannot be empty") Long productId);
}
