package com.omarahmed42.cart.message.listener;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarahmed42.cart.dto.message.Message;
import com.omarahmed42.cart.dto.response.CartItemResponse;
import com.omarahmed42.cart.message.payload.CartCheckoutPayload;
import com.omarahmed42.cart.message.producer.MessageSender;
import com.omarahmed42.cart.service.CartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageListener {

        private final ObjectMapper objectMapper;
        private final MessageSender messageSender;
        private final CartService cartService;

        @KafkaListener(id = "cart", topics = MessageSender.TOPIC)
        public void consume(@Payload String payload, @Header(name = "type", required = false) String messageType)
                        throws Exception {
                if (messageType == null)
                        return;
                log.info("Consuming message of type {} and payload {}", messageType, payload);
                if ("CartCheckoutEvent".equals(messageType)) {
                        placeCartOrder(
                                        objectMapper.readValue(payload,
                                                        new TypeReference<Message<CartCheckoutPayload>>() {
                                                        }));
                }
        }

        private void placeCartOrder(Message<CartCheckoutPayload> message) throws Exception {
                log.info("Retrieving cart items for message with correlation id {} of type {}",
                                message.getCorrelationId(),
                                message.getType());

                CartCheckoutPayload messagePayload = message.getPayload();

                List<CartItemResponse> cartItems = cartService.getCartItemsByUserId(messagePayload.getUserId());
                messagePayload.setItems(cartItems);

                message.setType("OrderPlacedEvent");
                log.info("Sending a message with correlation id {} of type {}", message.getCorrelationId(),
                                message.getType());

                messageSender.send(message);

                log.info("Message sent with correlation id {} of type {}", message.getCorrelationId(),
                                message.getType());
        }
}
