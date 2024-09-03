package com.omarahmed42.cart.message.payload;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.omarahmed42.cart.dto.response.CartItemResponse;

import lombok.Data;

@Data
public class CartCheckoutPayload implements Serializable {
    private Long id;
    private Long shippingAddressId;
    private Long billingAddressId;
    private String paymentId;
    private String userId;
    private List<CartItemResponse> orderItems = new ArrayList<>();

    public void setItems(List<CartItemResponse> cartItems) {
        this.orderItems = cartItems;
    }
}
