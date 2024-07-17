package com.omarahmed42.order.service;

import java.util.List;

import com.omarahmed42.order.dto.message.domain.Order;
import com.omarahmed42.order.dto.request.QueryFilter;
import com.omarahmed42.order.dto.response.OrderDetails;
import com.omarahmed42.order.dto.response.PaginationResult;
import com.omarahmed42.order.message.payload.item.PricedItem;

public interface OrderService {
    Order placeOrder(Order order);

    Order getOrder(Long orderId);

    OrderDetails getOrderDetails(Long orderId);

    PaginationResult<OrderDetails> getOrdersDetails(String userId, QueryFilter filter);

    OrderDetails updateOrderPrices(Long orderId, List<PricedItem> items);

    OrderDetails completeOrder(Long variable);
}
