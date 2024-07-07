package com.omarahmed42.inventory.inventory.message.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarahmed42.inventory.inventory.dto.message.Message;
import com.omarahmed42.inventory.inventory.dto.request.InventoryRequest;
import com.omarahmed42.inventory.inventory.message.payload.ProductCreatedPayload;
import com.omarahmed42.inventory.inventory.message.payload.ReserveStockPayload;
import com.omarahmed42.inventory.inventory.message.producer.MessageSender;
import com.omarahmed42.inventory.inventory.service.InventoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageListener {

    private final ObjectMapper objectMapper;
    private final MessageSender messageSender;
    private final InventoryService inventoryService;

    @KafkaListener(id = "inventory", topics = MessageSender.TOPIC)
    public void consume(@Payload String payload, @Header(name = "type", required = false) String messageType)
            throws Exception {
        if (messageType == null)
            return;
        log.info("Consuming message with of type {} with payload {}", messageType, payload);
        if ("ReserveInventoryEvent".equals(messageType)) {
            reserveStock(objectMapper.readValue(payload, new TypeReference<Message<ReserveStockPayload>>() {
            }));
        } else if ("ProductCreatedEvent".equals(messageType)) {
            createdInventoryItem(objectMapper.readValue(payload, new TypeReference<Message<ProductCreatedPayload>>() {
            }));
        }
    }

    private void reserveStock(Message<ReserveStockPayload> message) throws JsonProcessingException {
        log.info("Reserving stock for message with correlation id {} and of type {}", message.getCorrelationId(),
                message.getType());
        ReserveStockPayload receivedPayload = message.getPayload();
        inventoryService.reserveInventory(receivedPayload.getItems());

        ReserveStockPayload sendingPayload = new ReserveStockPayload();
        sendingPayload.setOrderId(receivedPayload.getOrderId());
        sendingPayload.setCorrelationId(message.getCorrelationId());

        Message<ReserveStockPayload> sendingMessage = new Message<>("StockReservedEvent",
                sendingPayload);
        sendingMessage.setCorrelationId(message.getCorrelationId());

        log.info("Publishing message with payload {}", objectMapper.writeValueAsString(sendingMessage));
        messageSender.send(sendingMessage);
        log.info("Message published with id {} and correlation id {}", sendingMessage.getId(),
                sendingMessage.getCorrelationId());
    }

    private void createdInventoryItem(Message<ProductCreatedPayload> message) {
        ProductCreatedPayload receivedPayload = message.getPayload();
        inventoryService
                .addInventoryItem(new InventoryRequest(receivedPayload.getProductId(), receivedPayload.getStock()));

    }
}
