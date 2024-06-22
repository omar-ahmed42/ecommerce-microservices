package com.omarahmed42.checkout.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CheckoutItem extends CheckoutOrder {
    @NotNull
    @Positive
    private Long productId;

    @Positive
    @NotNull
    private int quantity;
}
