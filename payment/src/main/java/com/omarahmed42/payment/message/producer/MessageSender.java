package com.omarahmed42.payment.message.producer;

import com.omarahmed42.payment.dto.message.Message;

public interface MessageSender {
    public static final String TOPIC = "ecommerce";

    void send(Message<?> message);
}
