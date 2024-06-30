package com.omarahmed42.inventory.inventory.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarahmed42.inventory.inventory.dto.message.InventoryError;
import com.omarahmed42.inventory.inventory.dto.message.InventoryItem;
import com.omarahmed42.inventory.inventory.dto.request.InventoryRequest;
import com.omarahmed42.inventory.inventory.dto.response.InventoryResponse;
import com.omarahmed42.inventory.inventory.exception.InsufficientStockException;
import com.omarahmed42.inventory.inventory.exception.InventoryItemNotFoundException;
import com.omarahmed42.inventory.inventory.exception.InventoryReservationException;
import com.omarahmed42.inventory.inventory.exception.QuantityExceedsStockException;
import com.omarahmed42.inventory.inventory.mapper.InventoryMapper;
import com.omarahmed42.inventory.inventory.model.Inventory;
import com.omarahmed42.inventory.inventory.repository.InventoryRepository;
import com.omarahmed42.inventory.inventory.service.InventoryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public InventoryResponse addInventoryItem(InventoryRequest inventoryRequest) {
        Inventory inventory = inventoryMapper.toEntity(inventoryRequest);
        if (inventory.getStock() == null || inventory.getStock() < 0)
            inventory.setStock(0);
        inventory = inventoryRepository.save(inventory);

        return inventoryMapper.toInventoryResponse(inventory);
    }

    @Override
    @Transactional
    public void deleteInventory(@NotNull(message = "Product ID cannot be empty") Long productId) {
        log.info("Deleting inventory for product with id {}", productId);
        inventoryRepository.deleteById(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventory(@NotNull(message = "Product ID cannot be empty") Long productId) {
        log.info("Retrieving inventory for product with id {}", productId);
        Inventory inventory = inventoryRepository.findById(productId).orElseThrow(
                () -> new InventoryItemNotFoundException("Inventory item with id " + productId + " not found"));
        return inventoryMapper.toInventoryResponse(inventory);
    }

    @Override
    @Transactional
    public InventoryResponse reserveInventory(@NotNull @Valid InventoryRequest inventoryRequest) {
        int quantity = inventoryRequest.stock();

        Inventory inventory = inventoryRepository
                .findById(inventoryRequest.productId())
                .orElseThrow(
                        () -> new InventoryItemNotFoundException(
                                "Inventory item with id " + inventoryRequest.productId() + " not found"));

        throwIfOutOfStock(inventory);
        throwIfQuantityExceedsStock(inventory, quantity);

        log.info("Reserving inventory for product with id {}", inventoryRequest.productId());
        inventory.setStock(inventory.getStock() - quantity);
        inventory = inventoryRepository.save(inventory);

        return inventoryMapper.toInventoryResponse(inventory);
    }

    private void throwIfOutOfStock(Inventory inventory) {
        if (inventory.getStock() == 0)
            throw new InsufficientStockException(
                    "Item with id " + inventory.getProductId() + " currently not in stock");
    }

    private void throwIfQuantityExceedsStock(Inventory inventory, Integer quantity) {
        if (inventory.getStock() - quantity < 0)
            throw new QuantityExceedsStockException("Quantity requested exceeds the stock quantity. Requested: "
                    + quantity + ", available: " + inventory.getStock() + ", for item with id "
                    + inventory.getProductId());
    }

    @Override
    @Transactional
    public InventoryResponse updateInventory(@NotNull Long id, @NotNull @Valid InventoryRequest inventoryRequest) {
        log.info("Updating inventory for product with id {}", inventoryRequest.productId());
        int stock = inventoryRequest.stock();

        Inventory inventory = inventoryRepository
                .findById(id)
                .orElseThrow(
                        () -> new InventoryItemNotFoundException(
                                "Inventory item with id " + inventoryRequest.productId() + " not found"));
        inventory.setStock(stock);
        inventory = inventoryRepository.save(inventory);
        return inventoryMapper.toInventoryResponse(inventory);
    }

    @Override
    @Transactional
    public void reserveInventory(List<InventoryItem> items) throws JsonProcessingException {
        List<Inventory> inventories = inventoryRepository.findAll(
                items.stream().filter(Objects::nonNull).map(InventoryItem::getProductId).collect(Collectors.toSet()));

        Map<Long, Inventory> idToItem = inventories.stream()
                .collect(Collectors.toMap(Inventory::getProductId, Function.identity()));

        List<Inventory> reservedInventory = new ArrayList<>(idToItem.size());
        List<InventoryError> reservationErrors = new ArrayList<>();
        for (InventoryItem item : items) {
            if (idToItem.containsKey(item.getProductId())) {
                Inventory inventory = idToItem.get(item.getProductId());

                int quantity = item.getQuantity();
                try {
                    throwIfOutOfStock(inventory);
                    throwIfQuantityExceedsStock(inventory, quantity);

                    inventory.setStock(inventory.getStock() - quantity);
                    reservedInventory.add(inventory);
                } catch (InsufficientStockException e) {
                    reservationErrors.add(new InventoryError(e.getMessage()));
                }
            }
        }

        if (!reservationErrors.isEmpty()) {
            throw new InventoryReservationException(objectMapper.writeValueAsString(reservationErrors));
        }

        inventoryRepository.saveAll(reservedInventory);
    }

}