package com.omarahmed42.checkout.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omarahmed42.checkout.dto.request.CheckoutCart;
import com.omarahmed42.checkout.dto.request.CheckoutItem;
import com.omarahmed42.checkout.service.CheckoutService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping("/items")
    public ResponseEntity<String> checkout(@RequestBody CheckoutItem checkoutItem) {
        return ResponseEntity.ok(checkoutService.checkout(checkoutItem));
    }

    @PostMapping("/cart")
    public ResponseEntity<String> checkout(@RequestBody CheckoutCart checkoutCart) {
        return ResponseEntity.ok(checkoutService.checkout(checkoutCart));
    }
}
