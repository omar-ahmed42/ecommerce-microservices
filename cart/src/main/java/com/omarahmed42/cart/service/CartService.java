package com.omarahmed42.cart.service;

import java.util.List;

import com.omarahmed42.cart.dto.request.CartItemCreation;
import com.omarahmed42.cart.dto.request.CartItemUpdate;
import com.omarahmed42.cart.dto.request.PaginationRequest;
import com.omarahmed42.cart.dto.response.CartItemResponse;
import com.omarahmed42.cart.dto.response.CartItemsCountResponse;
import com.omarahmed42.cart.dto.response.PaginationResult;

public interface CartService {

    CartItemResponse addToCart(CartItemCreation cartItem);

    CartItemResponse updateCartItem(Long cartItemId, CartItemUpdate itemRequest);

    void deleteCartItem(Long id);

    CartItemResponse getCartItem(Long id);

    PaginationResult<CartItemResponse> getCartItems(PaginationRequest paginationRequest);

    List<CartItemResponse> getCartItemsByUserId(String userId);

    CartItemsCountResponse countCartItems();

}
