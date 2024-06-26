package com.omarahmed42.payment.dto.request;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentRequest implements Serializable {
    private Long orderId;
    private BigDecimal totalCost;
    private Long paymentId;
    private String userId;
}
