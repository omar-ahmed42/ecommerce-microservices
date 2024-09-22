package com.omarahmed42.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.omarahmed42.order.conversion.deserializer.LongDeserializer;
import com.omarahmed42.order.conversion.serializer.LongSerializer;

@Configuration
public class JacksonConfiguration {

    @Bean
    SimpleModule longModule() {
        SimpleModule longModule = new SimpleModule("Long Module");
        longModule.addSerializer(Long.class, new LongSerializer());
        longModule.addSerializer(long.class, new LongSerializer());

        longModule.addDeserializer(Long.class, new LongDeserializer());
        longModule.addDeserializer(long.class, new LongDeserializer());
        return longModule;
    }
}
