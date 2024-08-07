package com.omarahmed42.checkout.dto.request;

import java.io.Serializable;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public abstract class CheckoutOrder implements Serializable {
    @NotNull
    private UUID paymentId;

    @NotNull
    @Positive
    private Long shippingAddressId;
    
    @NotNull
    @Positive
    private Long billingAddressId;
}
