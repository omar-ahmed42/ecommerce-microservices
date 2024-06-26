package com.omarahmed42.inventory.inventory.dto.message;

import java.io.Serializable;

import lombok.Data;

@Data
public class InventoryItem implements Serializable {
    private Long productId;
    private Integer quantity;
}
