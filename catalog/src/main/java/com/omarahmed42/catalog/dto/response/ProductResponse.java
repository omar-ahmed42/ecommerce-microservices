package com.omarahmed42.catalog.dto.response;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, String description, BigDecimal price, String sellerId,
        Integer categoryId) {
}
