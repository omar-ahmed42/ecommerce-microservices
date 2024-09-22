package com.omarahmed42.payment.dto.response;

import java.io.Serializable;
import java.util.UUID;

import com.omarahmed42.payment.dto.PaymentGatewayCustomerResponse;

import lombok.Data;

@Data
public class PaymentResponse implements Serializable {
    private UUID id;
    private String cardId;
    private String last4;
    private Short expMonth;
    private Integer expYear;
    private PaymentGatewayCustomerResponse gatewayCustomer;
    private String userId;
    private String brand;
}
