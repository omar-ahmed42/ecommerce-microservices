package com.omarahmed42.cart.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.omarahmed42.cart.model.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    void deleteByIdAndUserId(Long id, String userId);

    List<CartItem> findByUserId(String userId);

    Page<CartItem> findAllByUserId(String userId, PageRequest page);

}
