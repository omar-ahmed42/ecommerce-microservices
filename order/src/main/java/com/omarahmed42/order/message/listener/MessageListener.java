package com.omarahmed42.order.message.listener;

import java.util.Map;
import java.util.Objects;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
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
        if ("OrderPlacedEvent".equals(messageType)) {
            placeOrder(objectMapper.readValue(payload, new TypeReference<Message<Order>>() {
            }));
        } else if ("StockReservedEvent".equals(messageType)) {
            retrieveProductsPrices(
                    objectMapper.readValue(payload, new TypeReference<Message<ReserveStockPayload>>() {
                    }));
        } else if ("PricedProductsRetrievedEvent".equals(messageType)) {
            updateOrderCost(
                    objectMapper.readValue(payload, new TypeReference<Message<RetrievePricedItemsPayload>>() {
                    }));
        } else if ("PaymentRetrievedEvent".equals(messageType)) {
            completeOrder(objectMapper.readValue(payload, new TypeReference<Message<RetrievePaymentPayload>>() {
            }));
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
                .map(orderItem -> new Item(orderItem.getProductId(), orderItem.getQuantity())).toList());

        log.info("Before creating a new create instance command for place-order");
        ProcessInstanceEvent instanceEvent = zeebe.newCreateInstanceCommand()
                .bpmnProcessId("place-order")
                .latestVersion()
                .variables(payload.asMap(objectMapper))
                .send()
                .join();
        log.info("Instance event with the following info {}", instanceEvent.toString());
        log.info("After creating a new create instance command for place-order zeebe");
    }

    private void retrieveProductsPrices(Message<ReserveStockPayload> message) throws Exception {
        log.info("Retrieving Product prices for message with correlation id {} of type {}", message.getCorrelationId(),
                message.getType());
        ReserveStockPayload messagePayload = message.getPayload();

        Long orderId = messagePayload.getOrderId();
        Order order = orderService.getOrder(orderId);

        RetrievePricedItemsPayload payload = new RetrievePricedItemsPayload();
        payload.setOrderId(orderId);
        payload.setCorrelationId(message.getCorrelationId());
        payload.addPricedItems(
                order.getOrderItems().stream().map(orderItem -> new PricedItem(orderItem.getProductId())).toList());

        log.info("Before publishing a new message command for retrieving products");
        PublishMessageResponse publishedMessage = zeebe.newPublishMessageCommand()
                .messageName(message.getType())
                .correlationKey(message.getCorrelationId())
                .variables(payload.asMap(objectMapper))
                .send()
                .join();
        log.info("Published message key {} and tenant id {}", publishedMessage.getMessageKey(), publishedMessage.getTenantId());
        log.info("After publishing a new message command for retrieving products");
    }

    private void updateOrderCost(Message<RetrievePricedItemsPayload> message) throws Exception {
        log.info("Updating order cost for message with correlation id {} of type {}", message.getCorrelationId(),
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

    private void completeOrder(Message<RetrievePaymentPayload> message) {
        log.info("Completing order for message with correlation id {} of type {}", message.getCorrelationId(),
        message.getType());

        RetrievePaymentPayload payload = message.getPayload();

        zeebe.newPublishMessageCommand()
                .messageName(message.getType())
                .correlationKey(message.getCorrelationId())
                .variables(Map.of("orderId", payload.getOrderId(), "correlationId", payload.getCorrelationId()))
                .send()
                .join();
    }
}
