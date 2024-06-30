package com.omarahmed42.catalog.message.producer;

import com.omarahmed42.catalog.dto.message.Message;

public interface MessageSender {
    public static final String TOPIC = "ecommerce";

    void send(Message<?> message);
}
