package com.omarahmed42.inventory.unit.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarahmed42.inventory.inventory.dto.request.InventoryRequest;
import com.omarahmed42.inventory.inventory.exception.InsufficientStockException;
import com.omarahmed42.inventory.inventory.exception.InventoryItemNotFoundException;
import com.omarahmed42.inventory.inventory.exception.QuantityExceedsStockException;
import com.omarahmed42.inventory.inventory.mapper.InventoryMapper;
import com.omarahmed42.inventory.inventory.model.Inventory;
import com.omarahmed42.inventory.inventory.repository.InventoryRepository;
import com.omarahmed42.inventory.inventory.service.impl.InventoryServiceImpl;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMapper inventoryMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

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

    @Tag("deleteInventory")
    @DisplayName("Deleting an inventory item")
    @ParameterizedTest(name = "productId")
    @ValueSource(longs = { 1L, 2L, 1000L })
    void deleteInventory_should_delete_successfully(Long productId) {
        inventoryService.deleteInventory(productId);
        verify(inventoryRepository).deleteById(anyLong());
    }

    @Test
    @Tag("deleteInventory")
    @DisplayName("Deleting an inventory item with id null should violate constraint")
    void deleteInventory_null_id_should_violate_constraints() throws NoSuchMethodException, SecurityException {
        Method deleteInventoryMethod = InventoryServiceImpl.class.getMethod("deleteInventory", Long.class);
        Object[] parameterValues = { null };

        Set<ConstraintViolation<InventoryServiceImpl>> violations = validator.forExecutables()
                .validateParameters(inventoryService, deleteInventoryMethod, parameterValues);

        assertTrue(violations.size() > 0);
        assertEquals("Product ID cannot be empty",
                ((ConstraintViolation<InventoryServiceImpl>) violations.toArray()[0]).getMessage());
    }

    @Tag("getInventory")
    @DisplayName("Getting an inventory item")
    @ParameterizedTest(name = "productId")
    @ValueSource(longs = { 1L, 2L, 1000L })
    void getInventory_should_retrieve_the_item_successfully(Long productId) {
        when(inventoryRepository.findById(anyLong())).thenReturn(Optional.of(new Inventory()));

        inventoryService.getInventory(productId);
        verify(inventoryRepository).findById(anyLong());
        verify(inventoryMapper).toInventoryResponse(any(Inventory.class));
    }

    @Tag("getInventory")
    @DisplayName("Getting a non-present/non-existing inventory item - should throw InventoryItemNotFoundException")
    @Test
    void getInventory_for_non_existing_item_should_throw_InventoryNotFoundException() {
        Long productId = 1L;
        when(inventoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(InventoryItemNotFoundException.class, () -> inventoryService.getInventory(productId),
                "Inventory item with id " + productId + " not found");
        verify(inventoryRepository).findById(anyLong());
        verifyNoInteractions(inventoryMapper);
    }

    @Test
    @Tag("getInventory")
    @DisplayName("Getting an inventory item with id null should violate constraint")
    void getInventory_null_id_should_violate_constraints() throws NoSuchMethodException, SecurityException {
        Method getInventoryMethod = InventoryServiceImpl.class.getMethod("getInventory", Long.class);
        Object[] parameterValues = { null };

        Set<ConstraintViolation<InventoryServiceImpl>> violations = validator.forExecutables()
                .validateParameters(inventoryService, getInventoryMethod, parameterValues);

        assertTrue(violations.size() > 0);
        assertEquals("Product ID cannot be empty",
                ((ConstraintViolation<InventoryServiceImpl>) violations.toArray()[0]).getMessage());
    }

    @Test
    @Tag("updateInventory")
    @DisplayName("Updating an inventory item")
    void updateInventory_should_retrieve_the_item_successfully() {
        Long productId = 1L;
        InventoryRequest inventoryRequest = new InventoryRequest(productId, 1);

        when(inventoryRepository.findById(anyLong())).thenReturn(Optional.of(new Inventory()));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(new Inventory());

        inventoryService.updateInventory(productId, inventoryRequest);
        verify(inventoryRepository).findById(anyLong());
        verify(inventoryRepository).save(any(Inventory.class));
        verify(inventoryMapper).toInventoryResponse(any(Inventory.class));
    }

    @Tag("updateInventory")
    @DisplayName("Updating a non-present/non-existing inventory item - should throw InventoryItemNotFoundException")
    @Test
    void updateInventory_for_non_existing_item_should_throw_InventoryNotFoundException() {
        Long productId = 1L;
        InventoryRequest request = new InventoryRequest(productId, 1);
        when(inventoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(InventoryItemNotFoundException.class,
                () -> inventoryService.updateInventory(productId, request),
                "Inventory item with id " + productId + " not found");
        verify(inventoryRepository).findById(anyLong());
        verifyNoInteractions(inventoryMapper);
    }

    @Test
    @Tag("updateInventory")
    @DisplayName("Updating an inventory item with id null and inventory request null should violate constraint")
    void updateInventory_null_id_should_violate_constraints() throws NoSuchMethodException, SecurityException {
        Method updateInventoryMethod = InventoryServiceImpl.class.getMethod("updateInventory", Long.class,
                InventoryRequest.class);
        Object[] parameterValues = { null, null };

        Set<ConstraintViolation<InventoryServiceImpl>> violations = validator.forExecutables()
                .validateParameters(inventoryService, updateInventoryMethod, parameterValues);

        assertEquals(2, violations.size());
    }

    @Test
    @Tag("reserveInventory")
    @DisplayName("Reserving a non-existing inventory item - should throw InventoryNotFoundException")
    void reserveInventory_for_non_existing_item_should_throw_InventoryNotFoundException() {
        Long productId = 1L;
        InventoryRequest request = new InventoryRequest(productId, 1);
        when(inventoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(InventoryItemNotFoundException.class,
                () -> inventoryService.reserveInventory(request),
                "Inventory item with id " + productId + " not found");
        verify(inventoryRepository).findById(anyLong());
        verifyNoInteractions(inventoryMapper);
    }

    @Test
    @Tag("reserveInventory")
    @DisplayName("Reserving a non-existing inventory item - should throw InsufficientStockException")
    void reserveInventory_for_an_out_of_stock_item_should_throw_InsufficientStockException() {
        Long productId = 1L;
        InventoryRequest request = new InventoryRequest(productId, 1);

        Inventory outOfStockItem = new Inventory(1L, 0);
        when(inventoryRepository.findById(anyLong())).thenReturn(Optional.of(outOfStockItem));

        assertThrows(InsufficientStockException.class,
                () -> inventoryService.reserveInventory(request),
                "Item with id " + productId + " currently not in stock");
        verify(inventoryRepository).findById(anyLong());
        verify(inventoryRepository, times(0)).save(any(Inventory.class));
        verifyNoInteractions(inventoryMapper);
    }

    @Test
    @Tag("reserveInventory")
    @DisplayName("Reserving an item whose stock is less than requested/demanded/ordered quantity - should throw QuantityExceedsStockException")
    void reserveInventory_where_stock_less_than_quantity_should_throw_QuantityExceedsStockException() {
        Long productId = 1L;
        int stock = 2;
        int quantity = 5;
        InventoryRequest request = new InventoryRequest(productId, quantity);

        Inventory outOfStockItem = new Inventory(1L, stock);
        when(inventoryRepository.findById(anyLong())).thenReturn(Optional.of(outOfStockItem));

        assertThrows(QuantityExceedsStockException.class,
                () -> inventoryService.reserveInventory(request),
                "Quantity requested exceeds the stock quantity. Requested: "
                        + quantity + ", available: " + stock + ", for item with id "
                        + productId);
        verify(inventoryRepository).findById(anyLong());
        verify(inventoryRepository, times(0)).save(any(Inventory.class));
        verifyNoInteractions(inventoryMapper);
    }

    @Test
    @Tag("reserveInventory")
    @DisplayName("Reserving an inventory item with id null and inventory request null should violate constraint")
    void reserveInventory_null_id_should_violate_constraints() throws NoSuchMethodException, SecurityException {
        Method reserveInventoryMethod = InventoryServiceImpl.class.getMethod("reserveInventory",
                InventoryRequest.class);
        Object[] parameterValues = { null };

        Set<ConstraintViolation<InventoryServiceImpl>> violations = validator.forExecutables()
                .validateParameters(inventoryService, reserveInventoryMethod, parameterValues);

        assertEquals(1, violations.size());
    }

}
