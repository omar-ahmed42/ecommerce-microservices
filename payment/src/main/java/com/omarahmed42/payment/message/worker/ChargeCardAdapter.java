package com.omarahmed42.payment.message.worker;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarahmed42.payment.dto.request.PaymentRequest;
import com.omarahmed42.payment.message.payload.RetrievePaymentPayload;
import com.omarahmed42.payment.message.producer.MessageSender;
import com.omarahmed42.payment.service.PaymentService;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChargeCardAdapter {

    private final PaymentService paymentService;
    private final MessageSender messageSender;
    private final ObjectMapper objectMapper;

    @JobWorker(autoComplete = true, type = "retrieve-products-prices")
    public Map<String, String> handle(ActivatedJob job) {
        Map<String, Object> variablesAsMap = job.getVariablesAsMap();
        RetrievePaymentPayload payload = objectMapper.convertValue(variablesAsMap, RetrievePaymentPayload.class);

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(payload.getOrderId())
                .paymentId(payload.getPaymentId())
                .totalCost(payload.getTotalCost())
                .userId(payload.getUserId())
                .build();

        paymentService.chargeCard(paymentRequest);

        return Map.of("correlation_id", payload.getCorrelationId());
    }
}
