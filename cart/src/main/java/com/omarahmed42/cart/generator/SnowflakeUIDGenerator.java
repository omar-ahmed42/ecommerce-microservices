package com.omarahmed42.cart.generator;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.stereotype.Component;

import com.omarahmed42.cart.service.IdGeneratorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SnowflakeUIDGenerator implements IdentifierGenerator {

    private final IdGeneratorService<Long> idGeneratorService;

    @Override
    public Long generate(SharedSessionContractImplementor session, Object object) {
        Long id = idGeneratorService.generateId();
        log.info("Generated ID: {}", id);
        return id;
    }
}