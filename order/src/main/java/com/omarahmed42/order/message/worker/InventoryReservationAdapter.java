package com.omarahmed42.order.message.worker;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarahmed42.order.dto.message.Message;
import com.omarahmed42.order.message.payload.ReserveStockPayload;
import com.omarahmed42.order.message.producer.MessageSender;
import com.omarahmed42.order.service.OrderService;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InventoryReservationAdapter {

    private final OrderService orderService;
    private final MessageSender messageSender;
    private final ObjectMapper objectMapper;

    @JobWorker(autoComplete = true, type = "reserve-inventory")
    public Map<String, String> handle(ActivatedJob job) {
        Map<String, Object> variablesAsMap = job.getVariablesAsMap();
        ReserveStockPayload payload = objectMapper.convertValue(variablesAsMap, ReserveStockPayload.class);

        Message<ReserveStockPayload> message = new Message<>("ReserveInventoryEvent", payload);
        // message.setCorrelationId((String) job.getVariable("correlation_id"));
        message.setCorrelationId(payload.getCorrelationId());
        message.getMetadata().put("purchase_type", (String) job.getVariable("purchase_type"));

        messageSender.send(message);
        return Map.of("correlation_id", message.getCorrelationId());
    }
}
