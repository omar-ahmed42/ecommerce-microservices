package com.omarahmed42.order.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.omarahmed42.order.dto.request.OrderRequest;
import com.omarahmed42.order.dto.response.OrderDetails;
import com.omarahmed42.order.enums.OrderStatus;
import com.omarahmed42.order.exception.InsufficientStockException;
import com.omarahmed42.order.exception.InsufficientStockToFulfillOrderException;
import com.omarahmed42.order.exception.InvalidInputException;
import com.omarahmed42.order.exception.OrderNotFoundException;
import com.omarahmed42.order.mapper.OrderMapper;
import com.omarahmed42.order.message.payload.item.PricedItem;
import com.omarahmed42.order.model.Order;
import com.omarahmed42.order.model.OrderItem;
import com.omarahmed42.order.repository.OrderRepository;
import com.omarahmed42.order.service.OrderService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public void placeOrder(@Valid OrderRequest orderRequest) {
        if (orderRequest == null)
            throw new InvalidInputException("Order cannot be empty");
        if (orderRequest.productId() == null)
            throw new InvalidInputException("Product ID cannot be empty");
        if (orderRequest.quantity() == null)
            throw new InvalidInputException("Quantity cannot be empty");
        if (orderRequest.quantity() < 1)
            throw new InvalidInputException("Quantity cannot be less than 1");

        lockProductInInventory(orderRequest.productId());
        Integer stock = findStock(orderRequest.productId());
        if (stock <= 0) {
            releaseLockFromProductInInventory(orderRequest.productId());
            throw new InsufficientStockException("Not enough stock");
        } else if (stock < orderRequest.quantity()) {
            releaseLockFromProductInInventory(orderRequest.productId());
            throw new InsufficientStockToFulfillOrderException("Order quantity exceeds available stock");
        }

        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        // orderItem.setCost(orderRequest.quantity() * ;
        orderItem.setProductId(orderRequest.productId());

        order = orderRepository.save(order);
    }

    private void lockProductInInventory(@NotNull(message = "Product ID cannot be empty") Long productId) {
        throw new UnsupportedOperationException("Unimplemented method 'lockProductInInventory'");
    }

    private Integer findStock(@NotNull(message = "Product ID cannot be empty") Long productId) {
        throw new UnsupportedOperationException("Unimplemented method 'findStock'");
    }

    private void releaseLockFromProductInInventory(@NotNull(message = "Product ID cannot be empty") Long productId) {
        throw new UnsupportedOperationException("Unimplemented method 'releaseLockFromProductInInventory'");
    }

    @Override
    @Transactional
    public com.omarahmed42.order.dto.message.domain.Order placeOrder(
            com.omarahmed42.order.dto.message.domain.Order order) {
        Order orderEntity = new Order();
        orderEntity.setBillingAddressId(order.getBillingAddressId());
        orderEntity.setShippingAddressId(order.getShippingAddressId());
        orderEntity.setStatus(OrderStatus.PENDING);
        orderEntity.setUserId(order.getUserId());
        orderEntity.setPaymentId(order.getPaymentId());

        List<OrderItem> orderItems = order.getOrderItems().stream().filter(Objects::nonNull).map(item -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(item.getProductId());
            orderItem.setQuantity(item.getQuantity());
            return orderItem;
        }).collect(Collectors.toList());

        orderEntity.addItems(orderItems);
        orderEntity = orderRepository.save(orderEntity);
        return orderMapper.toOrder(orderEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public com.omarahmed42.order.dto.message.domain.Order getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + orderId + " not found"));
        return orderMapper.toOrder(order);
    }

    @Override
    @Transactional
    public OrderDetails updateOrderPrices(Long orderId,
            List<PricedItem> pricedItems) {
        Order order = orderRepository.findOrderAndOrderItemsById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + orderId + " not found"));

        Map<Long, OrderItem> productIdToItem = new HashMap<>();
        for (OrderItem item : order.getOrderItems()) {
            productIdToItem.put(item.getProductId(), item);
        }

        BigDecimal totalOrderCost = BigDecimal.ZERO;
        for (PricedItem pricedItem : pricedItems) {
            OrderItem orderItem = productIdToItem.get(pricedItem.getProductId());
            if (orderItem == null)
                continue;
            orderItem.setPrice(pricedItem.getPrice());
            BigDecimal cost = pricedItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            orderItem.setCost(cost);

            totalOrderCost = totalOrderCost.add(cost);
        }

        order.setTotalCost(totalOrderCost);
        return orderMapper.toOrderDetails(order);
    }

    @Override
    @Transactional
    public OrderDetails completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + orderId + " not found"));

        order.setStatus(OrderStatus.FULFILLED);
        order = orderRepository.save(order);
        return orderMapper.toOrderDetails(order);
    }

}
