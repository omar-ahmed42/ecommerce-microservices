package com.omarahmed42.checkout.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Data;

@Data
public class Order implements Serializable {
    private Long id;
    private Long shippingAddressId;
    private Long billingAddressId;
    private Long paymentId;
    private String userId;
    private List<OrderItem> orderItems = new ArrayList<>();

    public void addItems(Collection<OrderItem> items) {
        if (items == null || items.isEmpty())
            return;

        for (OrderItem item : items) {
            addItem(item);
        }
    }

    public void addItem(OrderItem item) {
        orderItems.add(item);
    }

    public void addItem(long productId, int quantity) {
        orderItems.add(new OrderItem(productId, quantity));
    }

    public void removeItem(Long productId) {
        orderItems.removeIf(item -> item.getProductId().equals(productId));
    }

    @Override
    public String toString() {
        return "Order [id=" + id + ", userId=" + userId + ", orderItems=" + orderItems + "]";
    }

}
