package com.omarahmed42.order.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omarahmed42.order.dto.message.domain.OrderItem;
import com.omarahmed42.order.dto.request.QueryFilter;
import com.omarahmed42.order.dto.response.OrderDetails;
import com.omarahmed42.order.dto.response.PaginationResult;
import com.omarahmed42.order.dto.response.TracedOrder;
import com.omarahmed42.order.service.OrderItemService;
import com.omarahmed42.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @GetMapping("/users/{user-id}/orders")
    public ResponseEntity<PaginationResult<OrderDetails>> getOrders(@PathVariable("user-id") UUID userId,
            QueryFilter filter) {
        return ResponseEntity.ok(orderService.getOrdersDetails(userId.toString(), filter));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDetails> getOrder(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.getOrderDetails(id));
    }

    @GetMapping("/orders/{id}/items")
    public ResponseEntity<List<OrderItem>> getOrderItems(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderItemService.getOrderItemsByOrderId(id));
    }
    

    @GetMapping("/orders/trace/{correlation-id}")
    public ResponseEntity<TracedOrder> traceOrder(@PathVariable("correlation-id") UUID correlationID) {
        return ResponseEntity.ok(orderService.getTracedOrder(correlationID));
    }

}