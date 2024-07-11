package com.omarahmed42.order.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import com.omarahmed42.order.enums.OrderStatus;
import com.omarahmed42.order.generator.SnowflakeUIDGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order implements Serializable {
    @Id
    @GenericGenerator(name = "snowflake_id_generator", type = SnowflakeUIDGenerator.class)
    @GeneratedValue(generator = "snowflake_id_generator")
    private Long id;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "shipping_address_id")
    private Long shippingAddressId;

    @Column(name = "billing_address_id")
    private Long billingAddressId;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "user_id")
    private String userId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    public void addItems(Collection<OrderItem> items) {
        if (items == null || items.isEmpty())
            return;

        for (OrderItem item : items) {
            addItem(item);
        }
    }

    public void addItem(OrderItem item) {
        item.setOrder(this);
        orderItems.add(item);
    }

    public void removeItem(Long productId) {
        orderItems.removeIf(item -> item.getProductId().equals(productId));
    }
}
