package com.omarahmed42.cart.message.producer;

import com.omarahmed42.cart.dto.message.Message;

public interface MessageSender {
    public static final String TOPIC = "ecommerce";

    void send(Message<?> message);
}
