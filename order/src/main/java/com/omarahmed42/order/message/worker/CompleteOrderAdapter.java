package com.omarahmed42.order.message.worker;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.omarahmed42.order.dto.message.Message;
import com.omarahmed42.order.dto.response.OrderDetails;
import com.omarahmed42.order.message.producer.MessageSender;
import com.omarahmed42.order.service.OrderService;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CompleteOrderAdapter {

    private final OrderService orderService;

    private final MessageSender messageSender;

    @JobWorker(autoComplete = true, type = "complete-order")
    public Map<String, String> handle(ActivatedJob job) {
        OrderDetails orderDetails = orderService.completeOrder((Long) job.getVariable("orderId"));

        Message<OrderDetails> message = new Message<>("OrderCompletedEvent", orderDetails,
                (String) job.getVariable("correlationId"));

        messageSender.send(message);
        return Map.of("correlation_id", message.getCorrelationId());
    }
}
