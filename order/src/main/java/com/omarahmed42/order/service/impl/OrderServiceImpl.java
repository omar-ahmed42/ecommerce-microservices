package com.omarahmed42.order.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.omarahmed42.order.dto.request.QueryFilter;
import com.omarahmed42.order.dto.response.OrderDetails;
import com.omarahmed42.order.dto.response.PaginationResult;
import com.omarahmed42.order.dto.response.TracedOrder;
import com.omarahmed42.order.enums.OrderStatus;
import com.omarahmed42.order.exception.ForbiddenOrderAccessException;
import com.omarahmed42.order.exception.OrderNotFoundException;
import com.omarahmed42.order.mapper.OrderMapper;
import com.omarahmed42.order.message.payload.item.PricedItem;
import com.omarahmed42.order.model.Order;
import com.omarahmed42.order.model.OrderItem;
import com.omarahmed42.order.repository.OrderRepository;
import com.omarahmed42.order.service.OrderService;
import com.omarahmed42.order.utils.PageUtils;
import com.omarahmed42.order.utils.SecurityUtils;

import jakarta.validation.constraints.NotNull;
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
            com.omarahmed42.order.dto.message.domain.Order order, String correlationId) {
        log.info("Placing order");
        Order orderEntity = new Order();
        orderEntity.setBillingAddressId(order.getBillingAddressId());
        orderEntity.setShippingAddressId(order.getShippingAddressId());
        orderEntity.setStatus(OrderStatus.PENDING);
        orderEntity.setUserId(order.getUserId());
        orderEntity.setPaymentId(order.getPaymentId());
        orderEntity.setCorrelationId(UUID.fromString(correlationId));

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
    public OrderDetails getOrderDetails(@NotNull(message = "Order ID cannot be empty") Long orderId) {
        log.info("Getting order details for order id {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(getOrderNotFoundExceptionMessage(orderId)));

        String subject = SecurityUtils.getSubject();
        if (isOwner(order, subject) || SecurityUtils.isAdmin()) {
            return orderMapper.toOrderDetails(order);
        }

        logUnauthorizedAccess(subject, orderId);

        // Throw not found instead of forbidden for security purposes
        throw new OrderNotFoundException(getOrderNotFoundExceptionMessage(orderId));
    }

    private boolean isOwner(Order order, String subject) {
        return order.getUserId().equals(subject);
    }

    private void logUnauthorizedAccess(String subject, @NotNull(message = "Order ID cannot be empty") Long orderId) {
        if (subject != null)
            log.info("Unauthorized user with id {} tried to access order with id {}", subject, orderId);
        else
            log.info("Unauthorized access to order with id {}", orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResult<OrderDetails> getOrdersDetails(@NotNull(message = "User ID cannot be empty") String userId,
            QueryFilter filter) {
        String authenticatedUser = SecurityUtils.getSubject();
        if (authenticatedUser == null || (!userId.equals(authenticatedUser) && !SecurityUtils.isAdmin())) {
            throw new ForbiddenOrderAccessException("Forbidden access to orders for user id " + userId);
        }

        PageRequest page = PageUtils.getPages(filter);

        if (StringUtils.isNotBlank((filter.getSearch()))) {
            Optional<Long> orderId = parseIdFrom(filter);
            Page<Order> ordersPage = orderId.isPresent()
                    ? orderRepository.findByIdContainingAndUserIdEquals(orderId.get(), userId,
                            page)
                    : orderRepository.findByUserId(userId, page);
            return toOrderDetailsPage(ordersPage);
        }

        Page<Order> ordersPage = orderRepository.findAll(page);
        return toOrderDetailsPage(ordersPage);
    }

    private Optional<Long> parseIdFrom(QueryFilter queryFilter) {
        Long id = null;
        try {
            id = Long.parseLong(queryFilter.getSearch());
            return Optional.ofNullable(id);
        } catch (NumberFormatException e) {
            return Optional.ofNullable(id);
        }
    }

    private PaginationResult<OrderDetails> toOrderDetailsPage(Page<Order> ordersPage) {
        return new PaginationResult<>(orderMapper.toOrderDetailsList(ordersPage.getContent()),
                ordersPage.getNumberOfElements(),
                ordersPage.getTotalElements(),
                ordersPage.getTotalPages(),
                ordersPage.getNumber());

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

    @Override
    @Transactional(readOnly = true)
    public TracedOrder getTracedOrder(UUID correlationId) {
        log.info("Retrieving traced order");
        Order order = orderRepository.findByCorrelationId(correlationId).orElseThrow(OrderNotFoundException::new);
        // TODO: Add authorization and prevent IDOR from happening
        return orderMapper.toTracedOrder(order);
    }
}
