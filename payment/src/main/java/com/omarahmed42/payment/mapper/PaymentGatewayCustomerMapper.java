package com.omarahmed42.payment.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import com.omarahmed42.payment.dto.PaymentGatewayCustomerResponse;
import com.omarahmed42.payment.model.PaymentGatewayCustomer;

@Mapper(componentModel = "spring")
public interface PaymentGatewayCustomerMapper {
    PaymentGatewayCustomer toEntity(PaymentGatewayCustomerResponse response);

    @InheritInverseConfiguration
    PaymentGatewayCustomerResponse toPaymentGatewayCustomerResponse(PaymentGatewayCustomer paymentGatewayCustomer);
}
