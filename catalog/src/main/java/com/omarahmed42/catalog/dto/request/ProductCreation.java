package com.omarahmed42.catalog.dto.request;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductCreation(
        @NotNull(message = "Title cannot be empty") @Size(min = 10, max = 200, message = "Title should be between 10 and 200 characters") @JsonAlias({"name", "title"}) String name,
        @Size(max = 2_000, message = "Product description should be at most 2,000 characters") String description,
        @NotNull(message = "Product price cannot be empty") BigDecimal price,
        @NotNull Integer categoryId) {
}
