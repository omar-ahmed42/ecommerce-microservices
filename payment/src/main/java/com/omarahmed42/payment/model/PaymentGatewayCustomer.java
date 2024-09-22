package com.omarahmed42.payment.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.omarahmed42.payment.enums.PaymentGatewayType;
import com.omarahmed42.payment.generator.SnowflakeUIDGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EntityListeners(AuditingEntityListener.class)
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

    private Long createdAt;

    @Column(name = "user_id")
    private String userId;

    @Column(insertable = false, name = "last_modified_at")
    @LastModifiedDate
    protected LocalDateTime lastModifiedAt;
}
