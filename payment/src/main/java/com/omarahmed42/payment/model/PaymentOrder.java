package com.omarahmed42.payment.model;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payment_order")
@Getter
@Setter
@NoArgsConstructor
public class PaymentOrder implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "payment_intent_id")
    private String paymentIntentId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private Long createdAt;

}
