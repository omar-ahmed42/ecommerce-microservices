package com.omarahmed42.inventory.inventory.message.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarahmed42.inventory.inventory.dto.message.Message;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KafkaMessageSender implements MessageSender {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(Message<?> message) {
        try {
            String payload = objectMapper.writeValueAsString(message);

            ProducerRecord<String, String> kafkaRecord = new ProducerRecord<>(TOPIC, payload);
            kafkaRecord.headers().add("type", message.getType().getBytes());
            kafkaTemplate.send(kafkaRecord);
        } catch (Exception e) {
            throw new RuntimeException("Could not transform and send message" + e.getMessage(), e);
        }
    }

}
