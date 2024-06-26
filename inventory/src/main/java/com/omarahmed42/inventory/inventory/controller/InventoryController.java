package com.omarahmed42.inventory.inventory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omarahmed42.inventory.inventory.dto.request.InventoryRequest;
import com.omarahmed42.inventory.inventory.dto.response.InventoryResponse;
import com.omarahmed42.inventory.inventory.service.InventoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/inventory/{id}")
    public ResponseEntity<InventoryResponse> getInventoryItem(@PathVariable("id") Long id) {
        return ResponseEntity.ok(inventoryService.getInventory(id));
    }

    @PutMapping("/inventory/{id}")
    public ResponseEntity<Void> updateInventoryItem(@PathVariable("id") Long id,
            @RequestBody InventoryRequest inventoryRequest) {
        inventoryService.updateInventory(id, inventoryRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/inventory/{id}")
    public ResponseEntity<Void> deleteInventoryItem(@PathVariable("id") Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}
