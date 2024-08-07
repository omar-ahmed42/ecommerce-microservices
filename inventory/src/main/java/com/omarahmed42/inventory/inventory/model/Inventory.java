package com.omarahmed42.inventory.inventory.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Inventory implements Serializable {
    @Id
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "stock")
    private Integer stock;
    @Column(name = "created_at")
    private Long createdAt;
    @Column(name = "modified_at")
    private Long modifiedAt;

    public Inventory(Long productId, Integer stock) {
        this.productId = productId;
        this.stock = stock;
    }
}
