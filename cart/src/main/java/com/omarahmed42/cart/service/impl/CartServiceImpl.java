package com.omarahmed42.cart.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.omarahmed42.cart.dto.request.CartItemCreation;
import com.omarahmed42.cart.dto.request.CartItemUpdate;
import com.omarahmed42.cart.dto.request.PaginationRequest;
import com.omarahmed42.cart.dto.response.CartItemResponse;
import com.omarahmed42.cart.dto.response.CartItemsCountResponse;
import com.omarahmed42.cart.dto.response.PaginationResult;
import com.omarahmed42.cart.exception.CartItemNotFoundException;
import com.omarahmed42.cart.mapper.CartItemMapper;
import com.omarahmed42.cart.model.CartItem;
import com.omarahmed42.cart.repository.CartItemRepository;
import com.omarahmed42.cart.service.CartService;
import com.omarahmed42.cart.utils.PageUtils;
import com.omarahmed42.cart.utils.SecurityUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    @Transactional
    public CartItemResponse addToCart(CartItemCreation cartItemCreation) {
        log.info("Creating a new cart item");
        SecurityUtils.throwIfUnauthenticated();
        CartItem cartItem = cartItemMapper.toEntity(cartItemCreation);
        cartItem.setUserId(SecurityUtils.getSubject());
        cartItem = cartItemRepository.save(cartItem);
        log.info("CartItem created successfully");
        return cartItemMapper.toCartItemResponse(cartItem);
    }

    @Override
    @Transactional
    public CartItemResponse updateCartItem(Long cartItemId, CartItemUpdate cartItemUpdate) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(CartItemNotFoundException::new);
        cartItemMapper.toTargetEntity(cartItem, cartItemUpdate);
        cartItem = cartItemRepository.save(cartItem);
        return cartItemMapper.toCartItemResponse(cartItem);
    }

    @Override
    @Transactional(readOnly = true)
    public CartItemResponse getCartItem(Long id) {
        log.info("Retrieving cartItem {}", id);
        CartItem cartItem = cartItemRepository.findById(id).orElseThrow(CartItemNotFoundException::new);
        log.info("Retrieved cartItem {}", id);
        return cartItemMapper.toCartItemResponse(cartItem);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResult<CartItemResponse> getCartItems(PaginationRequest paginationRequest) {
        SecurityUtils.throwIfUnauthenticated();
        String userId = SecurityUtils.getSubject();
        Page<CartItem> page = cartItemRepository.findAllByUserId(userId, PageUtils.getPage(paginationRequest));
        return new PaginationResult<>(cartItemMapper.toCartItemResponses(page.getContent()),
                page.getNumberOfElements(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber());
    }

    @Override
    @Transactional
    public void deleteCartItem(Long id) {
        SecurityUtils.throwIfUnauthenticated();
        String userId = SecurityUtils.getSubject();
        cartItemRepository.deleteByIdAndUserId(id, userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CartItemResponse> getCartItemsByUserId(String userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        return cartItemMapper.toCartItemResponses(cartItems);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CartItemsCountResponse countCartItems() {
        SecurityUtils.throwIfUnauthenticated();
        return new CartItemsCountResponse(cartItemRepository.countByUserId(SecurityUtils.getSubject()));
    }
}
