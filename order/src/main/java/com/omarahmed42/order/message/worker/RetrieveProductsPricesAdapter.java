package com.omarahmed42.order.message.worker;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarahmed42.order.dto.message.Message;
import com.omarahmed42.order.message.payload.RetrievePricedItemsPayload;
import com.omarahmed42.order.message.producer.MessageSender;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RetrieveProductsPricesAdapter {

    private final MessageSender messageSender;
    private final ObjectMapper objectMapper;

    @JobWorker(autoComplete = true, type = "retrieve-products-prices")
    public Map<String, String> handle(ActivatedJob job) {
        Map<String, Object> variablesAsMap = job.getVariablesAsMap();
        RetrievePricedItemsPayload payload = objectMapper.convertValue(variablesAsMap,
                RetrievePricedItemsPayload.class);

        Message<RetrievePricedItemsPayload> message = new Message<>("RetrievePricedProductsEvent", payload);
        message.setCorrelationId(payload.getCorrelationId());

        messageSender.send(message);
        return Map.of("correlation_id", message.getCorrelationId());
    }
}
