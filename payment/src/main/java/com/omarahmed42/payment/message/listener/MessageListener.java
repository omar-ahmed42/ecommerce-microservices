package com.omarahmed42.payment.message.listener;

import java.util.HashMap;
import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarahmed42.payment.dto.message.Message;
import com.omarahmed42.payment.message.payload.CardChargedPayload;
import com.omarahmed42.payment.message.payload.RetrievePaymentPayload;
import com.omarahmed42.payment.message.producer.MessageSender;

import io.camunda.zeebe.client.ZeebeClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageListener {

    private final ObjectMapper objectMapper;
    private final ZeebeClient zeebe;

    @KafkaListener(id = "payment", topics = MessageSender.TOPIC)
    public void consume(@Payload String payload, @Header(name = "type", required = false) String messageType)
            throws Exception {
        if (messageType == null)
            return;
        log.info("Consuming message of type {} and payload {}", messageType, payload);
        if ("RetrievePaymentEvent".equals(messageType)) {
            chargeCard(objectMapper.readValue(payload, new TypeReference<Message<RetrievePaymentPayload>>() {
            }));
        } else if ("CardChargedEvent".equals(messageType)) {
            retrievePayment(objectMapper.readValue(payload, new TypeReference<Message<CardChargedPayload>>() {
            }));
        }
    }

    private void chargeCard(Message<RetrievePaymentPayload> message) throws Exception {
        log.info("Charging card for message with correlation id {} of type {}", message.getCorrelationId(),
                message.getType());
        RetrievePaymentPayload messagePayload = message.getPayload();
        zeebe.newCreateInstanceCommand().bpmnProcessId("charge-card-process").latestVersion().variables(messagePayload.asMap())
                .send().join();
    }

    private void retrievePayment(Message<CardChargedPayload> message) {
        log.info("Publishing message to retrieve payment correlation id {}", message.getCorrelationId());
        CardChargedPayload payload = message.getPayload();

        Map<String, String> result = new HashMap<>();
        result.put("orderId", String.valueOf(payload.getOrderId()));
        result.put("reason", payload.getReason());
        result.put("correlationId", payload.getCorrelationId());

        zeebe.newPublishMessageCommand().messageName(message.getType()).correlationKey(message.getCorrelationId())
                .variables(result).send().join();
    }
}
