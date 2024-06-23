package com.omarahmed42.order.message.listener;

import java.util.Map;
import java.util.Objects;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omarahmed42.order.dto.message.Message;
import com.omarahmed42.order.dto.message.domain.Order;
import com.omarahmed42.order.message.payload.CalculateOrderCostPayload;
import com.omarahmed42.order.message.payload.ReserveStockPayload;
import com.omarahmed42.order.message.payload.RetrievePaymentPayload;
import com.omarahmed42.order.message.payload.RetrievePricedItemsPayload;
import com.omarahmed42.order.message.payload.item.Item;
import com.omarahmed42.order.message.payload.item.PricedItem;
import com.omarahmed42.order.message.producer.MessageSender;
import com.omarahmed42.order.service.OrderService;

import io.camunda.zeebe.client.ZeebeClient;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageListener {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;
    private final ZeebeClient zeebe;

    @KafkaListener(id = "order", topics = MessageSender.TOPIC)
    public void consume(String payload, @Header String messageType) throws Exception {
        if ("OrderPlacedEvent".equals(messageType)) {
            placeOrder(objectMapper.readValue(messageType, new TypeReference<Message<Order>>() {
            }));
        } else if ("StockReservedEvent".equals(messageType)) {
            retrieveProductsPrices(
                    objectMapper.readValue(messageType, new TypeReference<Message<ReserveStockPayload>>() {
                    }));
        } else if ("PricedProductsRetrievedEvent".equals(messageType)) {
            updateOrderCost(
                    objectMapper.readValue(messageType, new TypeReference<Message<RetrievePricedItemsPayload>>() {
                    }));
        } else if ("PaymentRetrievedEvent".equals(messageType)) {
            completeOrder(objectMapper.readValue(messageType, new TypeReference<Message<RetrievePaymentPayload>>() {
            }));
        }
    }

    private void placeOrder(Message<Order> message) throws Exception {
        Order order = message.getPayload();

        Order storedOrder = orderService.placeOrder(order);

        ReserveStockPayload payload = new ReserveStockPayload();
        payload.setOrderId(storedOrder.getId());
        payload.addItems(storedOrder.getOrderItems().stream().filter(Objects::nonNull)
                .map(orderItem -> new Item(orderItem.getProductId(), orderItem.getQuantity())).toList());

        zeebe.newCreateInstanceCommand()
                .bpmnProcessId("place-order")
                .latestVersion()
                .variables(payload.asMap(objectMapper))
                .send()
                .join();

    }

    private void retrieveProductsPrices(Message<ReserveStockPayload> message) throws Exception {
        ReserveStockPayload messagePayload = message.getPayload();

        Long orderId = messagePayload.getOrderId();
        Order order = orderService.getOrder(orderId);

        RetrievePricedItemsPayload payload = new RetrievePricedItemsPayload();
        payload.setOrderId(orderId);
        payload.setCorrelationId(message.getCorrelationId());
        payload.addPricedItems(
                order.getOrderItems().stream().map(orderItem -> new PricedItem(orderItem.getProductId())).toList());

        zeebe.newPublishMessageCommand()
                .messageName(message.getType())
                .correlationKey(message.getCorrelationId())
                .variables(payload.asMap(objectMapper))
                .send()
                .join();
    }

    private void updateOrderCost(Message<RetrievePricedItemsPayload> message) throws Exception {
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

    private void completeOrder(Message<RetrievePaymentPayload> message) {
        RetrievePaymentPayload payload = message.getPayload();

        zeebe.newPublishMessageCommand()
                .messageName(message.getType())
                .correlationKey(message.getCorrelationId())
                .variables(Map.of("orderId", payload.getOrderId(), "correlationId", payload.getCorrelationId()))
                .send()
                .join();
    }
}
