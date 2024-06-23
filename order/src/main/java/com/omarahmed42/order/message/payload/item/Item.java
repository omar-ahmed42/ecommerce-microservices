package com.omarahmed42.order.message.payload.item;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item implements Serializable {
    @Positive
    @NotNull
    private Long productId;

    @Positive
    @NotNull
    private int quantity;
}
