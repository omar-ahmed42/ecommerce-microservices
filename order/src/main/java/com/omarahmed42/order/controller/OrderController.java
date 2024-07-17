package com.omarahmed42.order.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omarahmed42.order.dto.request.QueryFilter;
import com.omarahmed42.order.dto.response.OrderDetails;
import com.omarahmed42.order.dto.response.PaginationResult;
import com.omarahmed42.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/users/{user-id}/orders")
    public ResponseEntity<PaginationResult<OrderDetails>> getOrders(@PathVariable("user-id") UUID userId,
            QueryFilter filter) {
        return ResponseEntity.ok(orderService.getOrdersDetails(userId.toString(), filter));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDetails> getOrder(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.getOrderDetails(id));
    }
}