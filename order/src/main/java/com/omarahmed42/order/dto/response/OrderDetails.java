package com.omarahmed42.order.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;

import com.omarahmed42.order.enums.OrderStatus;

import lombok.Data;

@Data
public class OrderDetails implements Serializable {
    private Long id;
    private BigDecimal totalCost;
    private OrderStatus status;
    private Long shippingAddressId;
    private Long billingAddressId;
    private Long paymentId;
    private String userId;

}
