package com.omarahmed42.payment.dto;

import java.io.Serializable;

import com.omarahmed42.payment.enums.PaymentGatewayType;

import lombok.Data;

@Data
public class PaymentGatewayCustomerResponse implements Serializable {
    private Long id;
    private String customerId;
    private PaymentGatewayType type;
    private Long createdAt;
    private Long userId;
}
