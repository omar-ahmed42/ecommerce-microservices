package com.omarahmed42.payment.message.worker;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.omarahmed42.payment.dto.request.PaymentRequest;
import com.omarahmed42.payment.message.payload.RetrievePaymentPayload;
import com.omarahmed42.payment.service.PaymentService;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChargeCardAdapter {

    private final PaymentService paymentService;

    @JobWorker(autoComplete = true, type = "charge-card", maxJobsActive = 15)//, timeout = 1000 * 60 * 10)
    public Map<String, String> handle(final ActivatedJob job) {
        log.info("Worker Handling charge-card");
        Map<String, Object> variablesAsMap = job.getVariablesAsMap();
        log.info("ChargeCardAdapter variablesAsMap {}", variablesAsMap.toString());
        RetrievePaymentPayload payload = RetrievePaymentPayload.fromMap(variablesAsMap);

        PaymentRequest paymentRequest = PaymentRequest.builder().orderId(payload.getOrderId())
                .paymentId(UUID.fromString(payload.getPaymentId())).totalCost(payload.getTotalCost())
                .userId(payload.getUserId()).build();

        paymentService.chargeCard(paymentRequest, payload.getCorrelationId());
        return Map.of("correlationId", payload.getCorrelationId());
    }
}
