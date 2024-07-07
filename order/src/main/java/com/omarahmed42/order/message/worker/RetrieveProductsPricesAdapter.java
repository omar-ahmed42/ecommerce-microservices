package com.omarahmed42.order.message.worker;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarahmed42.order.dto.message.Message;
import com.omarahmed42.order.message.payload.RetrievePricedItemsPayload;
import com.omarahmed42.order.message.producer.MessageSender;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class RetrieveProductsPricesAdapter {

    private final MessageSender messageSender;
    private final ObjectMapper objectMapper;

    @JobWorker(autoComplete = true, type = "retrieve-products-prices")
    public Map<String, String> handle(ActivatedJob job) throws JsonProcessingException {
        log.info("In retrieve-products-prices");
        Map<String, Object> variablesAsMap = job.getVariablesAsMap();
        log.info("RetrieveProductsPricesAdapter variablesAsMap {}", variablesAsMap.toString());

        RetrievePricedItemsPayload payload = objectMapper.readValue(job.getVariables(),
                RetrievePricedItemsPayload.class);

        Message<RetrievePricedItemsPayload> message = new Message<>("RetrievePricedProductsEvent", payload);
        message.setCorrelationId(payload.getCorrelationId());

        messageSender.send(message);
        return Map.of("RetrieveProductsPrices_correlation_id", message.getCorrelationId());
    }
}
