package com.omarahmed42.checkout.service.impl;

import org.springframework.stereotype.Service;

import com.omarahmed42.checkout.domain.Order;
import com.omarahmed42.checkout.dto.message.Message;
import com.omarahmed42.checkout.dto.request.CheckoutCart;
import com.omarahmed42.checkout.dto.request.CheckoutItem;
import com.omarahmed42.checkout.dto.request.CheckoutOrder;
import com.omarahmed42.checkout.enums.CheckoutType;
import com.omarahmed42.checkout.message.MessageSender;
import com.omarahmed42.checkout.service.CheckoutService;
import com.omarahmed42.checkout.utils.SecurityUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final MessageSender messageSender;
    private static final String CHECKOUT_TYPE = "checkout_type";

    @Override
    public String checkout(CheckoutItem checkoutItem) {
        Order order = new Order();
        setOrderDetails(order, checkoutItem, getAuthenticatedUserId());
        order.addItem(checkoutItem.getProductId(), checkoutItem.getQuantity());

        Message<Order> message = new Message<>("OrderPlacedEvent", order);
        message.getMetadata().put(CHECKOUT_TYPE, CheckoutType.DIRECT_PURCHASE.type());

        log.info("Send message from {} with id {} and correlation id {} of type {}", message.getSource(),
                message.getId(), message.getCorrelationId(), message.getType());

        messageSender.send(message);
        return "{\"correlationId\": \"" + message.getCorrelationId() + "\"}";
    }

    private void setOrderDetails(Order order, CheckoutOrder checkoutOrder, String userId) {
        order.setBillingAddressId(checkoutOrder.getBillingAddressId());
        order.setShippingAddressId(checkoutOrder.getShippingAddressId());
        order.setPaymentId(checkoutOrder.getPaymentId().toString());
        order.setUserId(userId);
    }

    private String getAuthenticatedUserId() {
        return SecurityUtils.getSubject();
    }

    @Override
    public String checkout(CheckoutCart checkoutCart) {
        Order order = new Order();
        setOrderDetails(order, checkoutCart, getAuthenticatedUserId());
        order.addItems(checkoutCart.getItems());

        Message<Order> message = new Message<>("CartCheckoutEvent", order);
        message.getMetadata().put(CHECKOUT_TYPE, CheckoutType.CART_PURCHASE.type());

        log.info("Send message from {} with id {} and correlation id {} of type {}", message.getSource(),
                message.getId(), message.getCorrelationId(), message.getType());

        messageSender.send(message);
        return "{\"correlationId\": \"" + message.getCorrelationId() + "\"}";
    }

}
