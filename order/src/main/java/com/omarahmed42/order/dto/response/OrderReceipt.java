package com.omarahmed42.order.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderReceipt implements Serializable {
    private Long id;
    private BigDecimal totalCost;
}
