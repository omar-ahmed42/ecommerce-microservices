package com.omarahmed42.checkout.dto.message;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Message<T> implements Serializable {
    private String id = UUID.randomUUID().toString();
    private String type;
    private String group = "ecommerce";
    private String source = "Checkout";
    private Date time = new Date();
    private T payload;
    private Map<String, String> metadata = new HashMap<>();
    private String correlationId = UUID.randomUUID().toString();

    public Message(String type, T payload) {
        this.type = type;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Message [id=" + id + ", type=" + type + ", group=" + group + ", source=" + source + ", time=" + time
                + ", payload=" + payload + ", correlationId=" + correlationId + "]";
    }
}
