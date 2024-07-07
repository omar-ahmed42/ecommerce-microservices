package com.omarahmed42.catalog.message.listener;

import java.util.List;
import java.util.Objects;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarahmed42.catalog.dto.message.Message;
import com.omarahmed42.catalog.message.payload.RetrievePricedItemsPayload;
import com.omarahmed42.catalog.message.payload.item.PricedItem;
import com.omarahmed42.catalog.message.producer.MessageSender;
import com.omarahmed42.catalog.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageListener {

    private final ObjectMapper objectMapper;
    private final ProductService productService;
    private final MessageSender messageSender;

    @KafkaListener(id = "catalog", topics = MessageSender.TOPIC)
    public void consume(@Payload String payload, @Header(name = "type", required = false) String messageType)
            throws Exception {
        if (messageType == null)
            return;
        log.info("Consuming message of type {} and payload {}", messageType, payload);
        if ("RetrievePricedProductsEvent".equals(messageType)) {
            retrievePricedProducts(
                    objectMapper.readValue(payload, new TypeReference<Message<RetrievePricedItemsPayload>>() {
                    }));
        }
    }

    private void retrievePricedProducts(Message<RetrievePricedItemsPayload> message) throws Exception {
        log.info("Retrieving Product prices for message with correlation id {} of type {}", message.getCorrelationId(),
                message.getType());

        RetrievePricedItemsPayload messagePayload = message.getPayload();
        List<Long> productsIds = message.getPayload().getItems().stream().filter(Objects::nonNull)
                .map(PricedItem::getProductId).filter(Objects::nonNull).toList();

        List<PricedItem> pricedProducts = productService.getPricedProducts(productsIds);
        messagePayload.setItems(pricedProducts);

        Message<RetrievePricedItemsPayload> messageToBeSent = new Message<>(
                "PricedProductsRetrievedEvent", messagePayload);
        messageToBeSent.setCorrelationId(message.getCorrelationId());

        log.info("Sending a message with correlation id {} of type {}", messageToBeSent.getCorrelationId(),
                messageToBeSent.getType());
        messageSender.send(messageToBeSent);

        log.info("Message sent with correlation id {} of type {}", messageToBeSent.getCorrelationId(),
                messageToBeSent.getType());
    }
}
