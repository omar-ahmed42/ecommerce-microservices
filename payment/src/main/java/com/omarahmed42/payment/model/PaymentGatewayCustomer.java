package com.omarahmed42.payment.model;

import java.io.Serializable;

import org.hibernate.annotations.GenericGenerator;

import com.omarahmed42.payment.enums.PaymentGatewayType;
import com.omarahmed42.payment.generator.SnowflakeUIDGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
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
    @GenericGenerator(name = "snowflake_id_generator", type = SnowflakeUIDGenerator.class)
    @GeneratedValue(generator = "snowflake_id_generator")
    private Long id;

    @Column(name = "customer_id")
    private String customerId;

    @Enumerated(EnumType.STRING)
    private PaymentGatewayType type;

    // private Long createdAt;

    @Column(name = "user_id")
    private String userId;
}
