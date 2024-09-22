package com.omarahmed42.order.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.omarahmed42.order.enums.OrderStatus;

public record TracedOrder(Long id, BigDecimal totalCost, OrderStatus status, List<TracedOrderItem> orderItems) implements Serializable {

}
