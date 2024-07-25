package com.omarahmed42.inventory.unit.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarahmed42.inventory.inventory.dto.request.InventoryRequest;
import com.omarahmed42.inventory.inventory.mapper.InventoryMapper;
import com.omarahmed42.inventory.inventory.model.Inventory;
import com.omarahmed42.inventory.inventory.repository.InventoryRepository;
import com.omarahmed42.inventory.inventory.service.impl.InventoryServiceImpl;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMapper inventoryMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @Tag("addInventoryItem")
    @DisplayName("Adding inventory item (stock should be >= 0 before saving to datastore) -> should succeed")
    @ParameterizedTest(name = "stock")
    @ValueSource(ints = { -1, 1 })
    @NullSource
    void addInventoryItem_should_return_InventoryResponse(Integer stock) {
        InventoryRequest inventoryRequest = new InventoryRequest(1L, stock);

        when(inventoryMapper.toEntity(any(InventoryRequest.class))).thenReturn(inventoryFrom(inventoryRequest));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(newInventoryValidStock(stock));

        inventoryService.addInventoryItem(inventoryRequest);

        verify(inventoryRepository).save(argThat(item -> item.getStock() != null && item.getStock() >= 0));
        verify(inventoryMapper).toEntity(any(InventoryRequest.class));
        verify(inventoryMapper).toInventoryResponse(argThat(Objects::nonNull));
    }

    private Inventory inventoryFrom(InventoryRequest request) {
        return new Inventory(request.productId(), request.stock());
    }

    private Inventory newInventoryValidStock(Integer nonNegativeStock) {
        Inventory inventory = new Inventory();
        inventory.setProductId(1L);
        if (nonNegativeStock == null || nonNegativeStock < 0)
            inventory.setStock(0);
        else
            inventory.setStock(nonNegativeStock);
        inventory.setCreatedAt(new Date().getTime());
        inventory.setModifiedAt(null);
        return inventory;
    }
}
