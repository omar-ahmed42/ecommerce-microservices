package com.omarahmed42.payment.message.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarahmed42.payment.dto.message.Message;
import com.omarahmed42.payment.message.payload.RetrievePaymentPayload;
import com.omarahmed42.payment.message.producer.MessageSender;
import com.omarahmed42.payment.service.PaymentService;

import io.camunda.zeebe.client.ZeebeClient;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageListener {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;
    private final ZeebeClient zeebe;

    @KafkaListener(id = "payment", topics = MessageSender.TOPIC)
    public void consume(String payload, @Header String messageType) throws Exception {
        if ("RetrievePayment".equals(messageType)) {
            chargeCard(objectMapper.readValue(payload, new TypeReference<Message<RetrievePaymentPayload>>() {
            }));
        }
    }

    private void chargeCard(Message<RetrievePaymentPayload> message) throws Exception {
        RetrievePaymentPayload messagePayload = message.getPayload();

        zeebe.newCreateInstanceCommand()
                .bpmnProcessId("charge-card")
                .latestVersion()
                .variables(messagePayload.asMap())
                .send()
                .join();

        // PaymentResponse payment = paymentService.getPayment(paymentId);
        // if (!messagePayload.getUserId().equals(payment.getUserId())) {
        // // TODO: Throw unauthorized exception
        // }

    }
}
