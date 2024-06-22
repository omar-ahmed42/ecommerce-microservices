package com.omarahmed42.checkout.service;

import org.springframework.web.bind.annotation.RequestBody;

import com.omarahmed42.checkout.dto.request.CheckoutCart;
import com.omarahmed42.checkout.dto.request.CheckoutItem;

public interface CheckoutService {
    String checkout(@RequestBody CheckoutItem checkoutItem);
    String checkout(@RequestBody CheckoutCart checkoutCart);
}
