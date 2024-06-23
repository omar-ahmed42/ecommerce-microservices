package com.omarahmed42.order.message.payload.item;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PricedItem implements Serializable {
    @NotNull
    @Positive
    private Long productId;
    @Positive
    private BigDecimal price;

    public PricedItem(Long productId) {
        this.productId = productId;
    }
}
