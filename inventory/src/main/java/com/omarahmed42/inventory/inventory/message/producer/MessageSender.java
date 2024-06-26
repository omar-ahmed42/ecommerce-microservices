package com.omarahmed42.inventory.inventory.message.producer;

import com.omarahmed42.inventory.inventory.dto.message.Message;

public interface MessageSender {
    public static final String TOPIC = "ecommerce";

    void send(Message<?> message);
}
