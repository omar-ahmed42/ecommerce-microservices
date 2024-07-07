package com.omarahmed42.order.message.worker;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarahmed42.order.dto.message.Message;
import com.omarahmed42.order.message.payload.ReserveStockPayload;
import com.omarahmed42.order.message.producer.MessageSender;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class InventoryReservationAdapter {

    private final MessageSender messageSender;
    private final ObjectMapper objectMapper;

    @JobWorker(autoComplete = true, type = "reserve-stock")
    public Map<String, String> handle(ActivatedJob job) throws JsonProcessingException {
        Map<String, Object> variablesAsMap = job.getVariablesAsMap();
        log.info("InventoryReservationAdapter variablesAsMap {}", variablesAsMap.toString());
        // ReserveStockPayload payload = objectMapper.convertValue(variablesAsMap,
        // ReserveStockPayload.class);
        ReserveStockPayload payload = ReserveStockPayload.fromMap(variablesAsMap, objectMapper);
        log.debug("Payload converted successfully");

        Message<ReserveStockPayload> message = new Message<>("ReserveInventoryEvent", payload);
        message.setCorrelationId(payload.getCorrelationId());
        // message.getMetadata().put("purchase_type", (String) job.getVariable("purchase_type"));

        messageSender.send(message);
        return Map.of("ReserveStock_correlation_id", message.getCorrelationId());
    }
}
