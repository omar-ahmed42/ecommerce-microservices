package com.omarahmed42.order.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;

public record TracedOrderItem(Long id, Long orderId, Long productId, BigDecimal cost, BigDecimal price,
        Integer quantity) implements Serializable {

}
