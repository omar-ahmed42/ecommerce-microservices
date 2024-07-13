package com.omarahmed42.order.message.listener;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarahmed42.order.dto.message.Message;
import com.omarahmed42.order.dto.message.domain.Order;
import com.omarahmed42.order.message.payload.CalculateOrderCostPayload;
import com.omarahmed42.order.message.payload.PaymentRetrievedPayload;
import com.omarahmed42.order.message.payload.ReserveStockPayload;
import com.omarahmed42.order.message.payload.RetrievePaymentPayload;
import com.omarahmed42.order.message.payload.RetrievePricedItemsPayload;
import com.omarahmed42.order.message.payload.item.Item;
import com.omarahmed42.order.message.payload.item.PricedItem;
import com.omarahmed42.order.message.producer.MessageSender;
import com.omarahmed42.order.service.OrderService;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.response.PublishMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageListener {

        private final OrderService orderService;
        private final ObjectMapper objectMapper;
        private final ZeebeClient zeebe;

        @KafkaListener(id = "order", topics = MessageSender.TOPIC)
        public void consume(@Payload String payload, @Header(name = "type", required = false) String messageType)
                        throws Exception {
                if (messageType == null)
                        return;
                log.info("Consuming message of type {} and payload {}", messageType, payload);
                switch (messageType) {
                        case "OrderPlacedEvent" ->
                                placeOrder(objectMapper.readValue(payload, new TypeReference<Message<Order>>() {
                                }));
                        case "StockReservedEvent" -> retrieveProductsPrices(
                                        objectMapper.readValue(payload,
                                                        new TypeReference<Message<ReserveStockPayload>>() {
                                                        }));
                        case "PricedProductsRetrievedEvent" -> updateOrderCost(
                                        objectMapper.readValue(payload,
                                                        new TypeReference<Message<RetrievePricedItemsPayload>>() {
                                                        }));
                        case "PrepareOrderPaymentEvent" -> retrievePayment(objectMapper.readValue(payload,
                                        new TypeReference<Message<RetrievePaymentPayload>>() {
                                        }));
                        case "PaymentRetrievedEvent" -> completeOrder(objectMapper.readValue(payload,
                                        new TypeReference<Message<PaymentRetrievedPayload>>() {
                                        }));
                        case "OrderCompletedEvent" -> log.info("Received OrderCompletedEvent");
                        default -> {
                                log.info("No matching event handler for {} ", messageType);
                        }
                }
        }

        private void placeOrder(Message<Order> message) throws Exception {
                log.info("Placing order for message with correlation id {} of type {}", message.getCorrelationId(),
                                message.getType());
                Order order = message.getPayload();

                Order storedOrder = orderService.placeOrder(order);
                log.info("Order placed for correlation id {}", message.getCorrelationId());

                ReserveStockPayload payload = new ReserveStockPayload();
                payload.setCorrelationId(message.getCorrelationId());
                payload.setOrderId(storedOrder.getId());
                payload.addItems(storedOrder.getOrderItems().stream().filter(Objects::nonNull)
                                .map(orderItem -> new Item(orderItem.getProductId(), orderItem.getQuantity()))
                                .toList());

                ProcessInstanceEvent instanceEvent = zeebe.newCreateInstanceCommand()
                                .bpmnProcessId("place-order")
                                .latestVersion()
                                .variables(payload.asMap(objectMapper))
                                .send()
                                .join();
                log.info("Instance event with the following info {}", instanceEvent.toString());
        }

        private void retrieveProductsPrices(Message<ReserveStockPayload> message) throws Exception {
                log.info("Retrieving Product prices for message with correlation id {} of type {}",
                                message.getCorrelationId(),
                                message.getType());
                ReserveStockPayload messagePayload = message.getPayload();

                Long orderId = messagePayload.getOrderId();
                Order order = orderService.getOrder(orderId);

                RetrievePricedItemsPayload payload = new RetrievePricedItemsPayload();
                payload.setOrderId(orderId);
                payload.setCorrelationId(message.getCorrelationId());
                payload.addPricedItems(
                                order.getOrderItems().stream()
                                                .map(orderItem -> new PricedItem(orderItem.getProductId())).toList());

                PublishMessageResponse publishedMessage = zeebe.newPublishMessageCommand()
                                .messageName(message.getType())
                                .correlationKey(message.getCorrelationId())
                                .variables(payload.asMap(objectMapper))
                                .send()
                                .join();
                log.info("Published message key {} and tenant id {}", publishedMessage.getMessageKey(),
                                publishedMessage.getTenantId());
        }

        private void updateOrderCost(Message<RetrievePricedItemsPayload> message) throws Exception {
                log.info("Updating order cost for message with correlation id {} of type {}",
                                message.getCorrelationId(),
                                message.getType());
                RetrievePricedItemsPayload messagePayload = message.getPayload();

                CalculateOrderCostPayload payload = new CalculateOrderCostPayload();
                payload.setOrderId(messagePayload.getOrderId());
                payload.setItems(messagePayload.getItems());
                payload.setCorrelationId(message.getCorrelationId());

                zeebe.newPublishMessageCommand()
                                .messageName(message.getType())
                                .correlationKey(message.getCorrelationId())
                                .variables(payload.asMap(objectMapper))
                                .send()
                                .join();
        }

        private void retrievePayment(Message<RetrievePaymentPayload> message) throws JsonProcessingException {
                log.info("Retrieving payment for message with correlation id {} of type {}",
                                message.getCorrelationId(),
                                message.getType());

                RetrievePaymentPayload payload = message.getPayload();
                payload.setCorrelationId(message.getCorrelationId());

                zeebe.newPublishMessageCommand()
                                .messageName(message.getType())
                                .correlationKey(message.getCorrelationId())
                                .variables(payload.asMap())
                                .send()
                                .join();
        }

        private void completeOrder(Message<PaymentRetrievedPayload> message) {
                log.info("Completing order for message with correlation id {} of type {}", message.getCorrelationId(),
                                message.getType());

                PaymentRetrievedPayload payload = message.getPayload();

                Map<String, String> map = new HashMap<>();
                map.put("orderId", String.valueOf(payload.getOrderId()));
                map.put("correlationId", message.getCorrelationId());
                PublishMessageResponse messageResponse = zeebe.newPublishMessageCommand()
                                .messageName(message.getType())
                                .correlationKey(message.getCorrelationId())
                                .timeToLive(Duration.ofSeconds(30))
                                .variables(map)
                                .send()
                                .join();
                log.info("Message response: Message Key {}", messageResponse.getMessageKey());
        }
}
