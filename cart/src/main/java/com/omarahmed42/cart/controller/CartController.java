package com.omarahmed42.cart.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.omarahmed42.cart.dto.request.CartItemCreation;
import com.omarahmed42.cart.dto.request.CartItemUpdate;
import com.omarahmed42.cart.dto.request.PaginationRequest;
import com.omarahmed42.cart.dto.response.CartItemResponse;
import com.omarahmed42.cart.dto.response.PaginationResult;
import com.omarahmed42.cart.enums.SortOrder;
import com.omarahmed42.cart.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping(value = "/cart")
    public ResponseEntity<Void> addToCart(@RequestBody CartItemCreation cartItem) {
        CartItemResponse cartItemResponse = cartService.addToCart(cartItem);
        return ResponseEntity.created(URI.create(cartItemResponse.id().toString())).build();
    }

    @PutMapping("/cart/{id}")
    public ResponseEntity<CartItemResponse> updateCartItem(@PathVariable Long id,
            @RequestBody CartItemUpdate cartItemUpdate) {
        return ResponseEntity.ok(cartService.updateCartItem(id, cartItemUpdate));
    }

    @DeleteMapping("/cart/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        cartService.deleteCartItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cart/{id}")
    public ResponseEntity<CartItemResponse> getCart(@PathVariable("id") Long id) {
        return ResponseEntity.ok(cartService.getCartItem(id));
    }

    @GetMapping("/cart")
    public ResponseEntity<PaginationResult<CartItemResponse>> getCartItems(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "15") Integer size, @RequestParam(defaultValue = "ASC") SortOrder sortOrder) {
        return ResponseEntity.ok(cartService.getCartItems(new PaginationRequest(page, size, sortOrder)));
    }

}
