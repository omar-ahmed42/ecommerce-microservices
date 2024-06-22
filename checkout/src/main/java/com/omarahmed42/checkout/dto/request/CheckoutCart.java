package com.omarahmed42.checkout.dto.request;

import java.util.ArrayList;
import java.util.List;

import com.omarahmed42.checkout.domain.OrderItem;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CheckoutCart extends CheckoutOrder {
    private List<OrderItem> items = new ArrayList<>();
}
