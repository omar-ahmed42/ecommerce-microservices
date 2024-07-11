package com.omarahmed42.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.omarahmed42.order.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value = "SELECT o.* FROM orders o LEFT JOIN order_items oi ON o.id = oi.order_id WHERE o.id = :order_id", nativeQuery = true)
    Optional<Order> findOrderAndOrderItemsById(@Param("order_id") Long orderId);
}
