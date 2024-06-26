package com.omarahmed42.payment.model;

import java.io.Serializable;

import com.omarahmed42.payment.enums.PaymentGatewayType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payment_gateway_customers")
@Getter
@Setter
@NoArgsConstructor
public class PaymentGatewayCustomer implements Serializable {
    @Id
    private Long id;

    @Column(name = "customer_id")
    private String customerId;

    @Enumerated(EnumType.STRING)
    private PaymentGatewayType type;

    // private Long createdAt;

    @Column(name = "user_id")
    private String userId;
}
