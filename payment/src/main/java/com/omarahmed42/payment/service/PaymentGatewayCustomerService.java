package com.omarahmed42.payment.service;

import java.util.Optional;

import com.omarahmed42.payment.model.PaymentGatewayCustomer;
import com.stripe.exception.StripeException;

public interface PaymentGatewayCustomerService {
    PaymentGatewayCustomer addPaymentGatewayCustomer() throws StripeException;
    PaymentGatewayCustomer getOrAddPaymentGatewayCustomer(String userId) throws StripeException;
    Optional<PaymentGatewayCustomer> getByCustomerId(String customer);
}
