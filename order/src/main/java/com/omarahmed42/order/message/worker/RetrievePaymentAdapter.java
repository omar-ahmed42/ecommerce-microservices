package com.omarahmed42.order.message.worker;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.omarahmed42.order.dto.message.Message;
import com.omarahmed42.order.message.payload.RetrievePaymentPayload;
import com.omarahmed42.order.message.producer.MessageSender;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class RetrievePaymentAdapter {

    private final MessageSender messageSender;

    @JobWorker(autoComplete = true, type = "retrieve-payment")
    public Map<String, String> handle(ActivatedJob job) throws JsonProcessingException {
        Map<String, Object> variablesAsMap = job.getVariablesAsMap();
        log.info("RetrievePaymentAdapter variablesAsMap {}", variablesAsMap.toString());
        RetrievePaymentPayload payload = RetrievePaymentPayload.fromMap(variablesAsMap);
        log.debug("Payload converted successfully");

        Message<RetrievePaymentPayload> message = new Message<>("RetrievePaymentEvent", payload);
        message.setCorrelationId(payload.getCorrelationId());
        messageSender.send(message);
        return Map.of("RetrievePayment_correlation_id", message.getCorrelationId());
    }
}
