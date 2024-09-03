package com.omarahmed42.cart.dto.response;

public record CartItemResponse(
                Long id,
                Long productId,
                Integer quantity,
                String userId) {
}
