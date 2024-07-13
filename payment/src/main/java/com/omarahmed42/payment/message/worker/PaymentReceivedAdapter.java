package com.omarahmed42.payment.message.worker;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.omarahmed42.payment.dto.message.Message;
import com.omarahmed42.payment.message.payload.PaymentRetrievedPayload;
import com.omarahmed42.payment.message.producer.MessageSender;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentReceivedAdapter {

    private final MessageSender messageSender;

    @JobWorker(autoComplete = true, type = "paymentReceived", maxJobsActive = 15)//, timeout = 1000 * 60 * 10, maxJobsActive = 2, requestTimeout = 10000, pollInterval = 1000, name = "Payment Received Worker")
    public void handle(final ActivatedJob job) {
        log.info("Worker Handling paymentReceived");
        Map<String, Object> variablesAsMap = job.getVariablesAsMap();

        log.info("PaymentReceivedAdapter variablesAsMap {}", variablesAsMap.toString());
        PaymentRetrievedPayload payload = PaymentRetrievedPayload.fromMap(variablesAsMap);

        Message<PaymentRetrievedPayload> message = new Message<>("PaymentRetrievedEvent", payload);
        message.setCorrelationId((String) variablesAsMap.get("correlationId"));
        messageSender.send(message);
    }
}
