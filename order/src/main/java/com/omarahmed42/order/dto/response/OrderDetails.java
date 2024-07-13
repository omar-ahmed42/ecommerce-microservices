package com.omarahmed42.order.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.omarahmed42.order.enums.OrderStatus;

import lombok.Data;

@Data
public class OrderDetails implements Serializable {
    private Long id;
    private BigDecimal totalCost;
    private OrderStatus status;
    private Long shippingAddressId;
    private Long billingAddressId;
    private String paymentId;
    private String userId;

    public Map<String, String> asMap() {
        Map<String, String> result = new HashMap<>();
        result.put("id", id.toString());
        result.put("totalCost", totalCost.toString());
        result.put("status", status.toString());
        result.put("shippingAddressId", shippingAddressId.toString());
        result.put("billingAddressId", billingAddressId.toString());
        result.put("paymentId", paymentId);
        result.put("userId", userId);
        return result;
    }

    public static OrderDetails fromMap(Map<String, Object> map) {
        OrderDetails orderDetails = new OrderDetails();
        if (map == null || map.isEmpty())
            return orderDetails;

        if (map.containsKey("orderId"))
            orderDetails.setId(Long.valueOf((String) map.get("orderId")));
        else
            orderDetails.setId(Long.valueOf((String) map.get("id")));

        if (map.containsKey("totalCost"))
            orderDetails.setTotalCost(new BigDecimal((String) map.get("totalCost")));
        if (map.containsKey("status"))
            orderDetails.setStatus(OrderStatus.valueOf((String) map.get("status")));
        if (map.containsKey("shippingAddressId"))
            orderDetails.setBillingAddressId(Long.valueOf((String) map.get("shippingAddressId")));
        if (map.containsKey("billingAddressId"))
            orderDetails.setBillingAddressId(Long.valueOf((String) map.get("billingAddressId")));
        if (map.containsKey("paymentId"))
            orderDetails.setPaymentId((String) map.get("paymentId"));
        if (map.containsKey("userId"))
            orderDetails.setUserId((String) map.get("userId"));

        return orderDetails;
    }

}
