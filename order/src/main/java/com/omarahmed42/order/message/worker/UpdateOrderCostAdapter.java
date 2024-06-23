package com.omarahmed42.order.message.worker;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarahmed42.order.dto.message.Message;
import com.omarahmed42.order.dto.response.OrderDetails;
import com.omarahmed42.order.message.payload.CalculateOrderCostPayload;
import com.omarahmed42.order.message.payload.RetrievePaymentPayload;
import com.omarahmed42.order.message.producer.MessageSender;
import com.omarahmed42.order.service.OrderService;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UpdateOrderCostAdapter {

    private final OrderService orderService;
    private final MessageSender messageSender;
    private final ObjectMapper objectMapper;

    @JobWorker(autoComplete = true, type = "update-order-cost")
    public Map<String, String> handle(ActivatedJob job) {
        Map<String, Object> variablesAsMap = job.getVariablesAsMap();
        CalculateOrderCostPayload payload = objectMapper.convertValue(variablesAsMap,
                CalculateOrderCostPayload.class);

        OrderDetails orderDetails = orderService.updateOrderPrices(payload.getOrderId(), payload.getItems());

        RetrievePaymentPayload retrievePaymentPayload = new RetrievePaymentPayload();
        retrievePaymentPayload.setOrderId(orderDetails.getId());
        retrievePaymentPayload.setPaymentId(orderDetails.getPaymentId());
        retrievePaymentPayload.setUserId(orderDetails.getUserId());
        retrievePaymentPayload.setTotalCost(orderDetails.getTotalCost());
        retrievePaymentPayload.setCorrelationId(payload.getCorrelationId());

        Message<RetrievePaymentPayload> message = new Message<>("PrepareOrderPaymentEvent", retrievePaymentPayload);
        message.setCorrelationId(payload.getCorrelationId());

        messageSender.send(message);
        return Map.of("correlation_id", message.getCorrelationId());
    }
}
