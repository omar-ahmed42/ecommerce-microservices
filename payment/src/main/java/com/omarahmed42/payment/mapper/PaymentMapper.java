package com.omarahmed42.payment.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import com.omarahmed42.payment.dto.response.PaymentResponse;
import com.omarahmed42.payment.model.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    Payment toEntity(PaymentResponse response);

    @InheritInverseConfiguration
    PaymentResponse toPaymentResponse(Payment payment);
}
