package com.omarahmed42.order.message.producer;

import com.omarahmed42.order.dto.message.Message;

public interface MessageSender {
    public static final String TOPIC = "ecommerce";
    void send(Message<?> message);
}
