package com.omarahmed42.order.dto.message.domain;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem implements Serializable {
    private Long id;

    @Positive
    @NotNull
    private Long productId;

    @Positive
    @NotNull
    private int quantity;

    public OrderItem(@Positive @NotNull Long productId, @Positive @NotNull int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

}
