package com.omarahmed42.checkout.message;

import com.omarahmed42.checkout.dto.message.Message;

public interface MessageSender {
    void send(Message<?> message);
}
