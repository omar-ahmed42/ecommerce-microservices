package com.omarahmed42.catalog.dto.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductResponse(Long id, @JsonProperty("title") String name, String description, BigDecimal price, String sellerId,
        Integer categoryId, AttachmentResponse thumbnail) {
}
