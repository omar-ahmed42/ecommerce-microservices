package com.omarahmed42.checkout.dto.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public abstract class CheckoutOrder implements Serializable {
    @NotNull
    @Positive
    private Long paymentId;

    @NotNull
    @Positive
    private Long shippingAddressId;
    
    @NotNull
    @Positive
    private Long billingAddressId;
}
