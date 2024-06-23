package com.omarahmed42.order.service;

import java.util.List;

import com.omarahmed42.order.dto.message.domain.OrderItem;

public interface OrderItemService {
    List<OrderItem> getOrderItemsByOrderId(Long orderId);
}
