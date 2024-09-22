package com.omarahmed42.checkout.conversion.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class LongDeserializer extends StdDeserializer<Long> {

    public LongDeserializer() {
        super(Long.class);
    }

    @Override
    public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String value = p.getValueAsString();
        if (value == null)
            return null;
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            ctxt.handleUnexpectedToken(_valueType, p);
            return null;
        }
    }
}
