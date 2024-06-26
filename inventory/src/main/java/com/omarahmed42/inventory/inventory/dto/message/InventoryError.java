package com.omarahmed42.inventory.inventory.dto.message;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InventoryError implements Serializable {
    private String message;
}
