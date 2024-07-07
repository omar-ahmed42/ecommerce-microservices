package com.omarahmed42.order.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.omarahmed42.order.dto.response.OrderDetails;
import com.omarahmed42.order.enums.OrderStatus;
import com.omarahmed42.order.exception.OrderNotFoundException;
import com.omarahmed42.order.mapper.OrderMapper;
import com.omarahmed42.order.message.payload.item.PricedItem;
import com.omarahmed42.order.model.Order;
import com.omarahmed42.order.model.OrderItem;
import com.omarahmed42.order.repository.OrderRepository;
import com.omarahmed42.order.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public com.omarahmed42.order.dto.message.domain.Order placeOrder(
            com.omarahmed42.order.dto.message.domain.Order order) {
        log.info("Placing order");
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
        log.info("Order stored successfully");
        return orderMapper.toOrder(orderEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public com.omarahmed42.order.dto.message.domain.Order getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(getOrderNotFoundExceptionMessage(orderId)));
        return orderMapper.toOrder(order);
    }

    private String getOrderNotFoundExceptionMessage(Long orderId) {
        return "Order with id " + orderId + " not found";
    }

    @Override
    @Transactional
    public OrderDetails updateOrderPrices(Long orderId,
            List<PricedItem> pricedItems) {
        Order order = orderRepository.findOrderAndOrderItemsById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(getOrderNotFoundExceptionMessage(orderId)));

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
                .orElseThrow(() -> new OrderNotFoundException(getOrderNotFoundExceptionMessage(orderId)));

        order.setStatus(OrderStatus.FULFILLED);
        order = orderRepository.save(order);
        return orderMapper.toOrderDetails(order);
    }

}
