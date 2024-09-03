package com.omarahmed42.cart.model;

import java.io.Serializable;

import org.hibernate.annotations.GenericGenerator;

import com.omarahmed42.cart.generator.SnowflakeUIDGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "cart_items")
@Entity
@Getter
@Setter
public class CartItem implements Serializable {
    @Id
    @GenericGenerator(name = "snowflake_id_generator", type = SnowflakeUIDGenerator.class)
    @GeneratedValue(generator = "snowflake_id_generator")
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "user_id")
    private String userId;
}
