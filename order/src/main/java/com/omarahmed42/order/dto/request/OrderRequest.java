package com.omarahmed42.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(@NotNull(message = "Product ID cannot be empty") Long productId,
                @NotNull(message = "Quantity cannot be empty") @Min(value = 1, message = "Quantity cannot be less than 1") Integer quantity) {
}