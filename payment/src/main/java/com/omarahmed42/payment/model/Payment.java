package com.omarahmed42.payment.model;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class Payment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "card_id")
    private String cardId;

    @Column(name = "last_4", length = 4)
    private String last4;

    @Column(name = "exp_month")
    private Short expMonth;

    @Column(name = "exp_year")
    private Integer expYear;

    @ManyToOne
    @JoinColumn(name = "payment_gateway_customer_id")
    private PaymentGatewayCustomer gatewayCustomer;

    @Column(name = "user_id")
    private String userId;
}
