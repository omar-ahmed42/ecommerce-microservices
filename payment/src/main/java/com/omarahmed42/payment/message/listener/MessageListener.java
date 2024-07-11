package com.omarahmed42.payment.message.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarahmed42.payment.dto.message.Message;
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
        }
    }

    private void chargeCard(Message<RetrievePaymentPayload> message) throws Exception {
        log.info("Charging card for message with correlation id {} of type {}", message.getCorrelationId(),
        message.getType());
        RetrievePaymentPayload messagePayload = message.getPayload();

        zeebe.newCreateInstanceCommand().bpmnProcessId("charge-card").latestVersion().variables(messagePayload.asMap())
                .send().join();
    }
}
