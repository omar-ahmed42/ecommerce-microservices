package com.omarahmed42.inventory.inventory.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.omarahmed42.inventory.inventory.dto.request.InventoryRequest;
import com.omarahmed42.inventory.inventory.dto.response.InventoryResponse;
import com.omarahmed42.inventory.inventory.model.Inventory;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventoryMapper {
    Inventory toEntity(InventoryRequest inventoryRequest);

    @InheritInverseConfiguration
    InventoryRequest toInventoryRequest(Inventory inventory);

    InventoryResponse toInventoryResponse(Inventory inventory);

    @InheritInverseConfiguration
    Inventory toEntity(InventoryResponse inventoryResponse);
}
