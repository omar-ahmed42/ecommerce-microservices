package com.omarahmed42.payment.service.impl;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import com.omarahmed42.payment.enums.PaymentGatewayType;
import com.omarahmed42.payment.model.PaymentGatewayCustomer;
import com.omarahmed42.payment.repository.PaymentGatewayCustomerRepository;
import com.omarahmed42.payment.service.PaymentGatewayCustomerService;
import com.omarahmed42.payment.utils.SecurityUtils;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentGatewayCustomerServiceImpl implements PaymentGatewayCustomerService {

    private final PaymentGatewayCustomerRepository paymentGatewayCustomerRepository;

    @Override
    public PaymentGatewayCustomer addPaymentGatewayCustomer() throws StripeException {
        String userId = SecurityUtils.getSubject();
        return getOrAddPaymentGatewayCustomer(userId);
    }

    @Override
    public PaymentGatewayCustomer getOrAddPaymentGatewayCustomer(String userId) throws StripeException {
        Optional<PaymentGatewayCustomer> maybeGatewayCustomer = paymentGatewayCustomerRepository
                .findByUserId(userId);

        PaymentGatewayCustomer paymentGatewayCustomer = null;
        if (maybeGatewayCustomer.isEmpty()) {
            Customer customer = Customer
                    .create(CustomerCreateParams.builder().setMetadata(Map.of("userId", userId)).build());

            paymentGatewayCustomer = new PaymentGatewayCustomer();
            paymentGatewayCustomer.setType(PaymentGatewayType.STRIPE);
            paymentGatewayCustomer.setUserId(userId);
            paymentGatewayCustomer.setCustomerId(customer.getId());
            paymentGatewayCustomer = paymentGatewayCustomerRepository.save(paymentGatewayCustomer);
        } else {
            paymentGatewayCustomer = maybeGatewayCustomer.get();
        }

        return paymentGatewayCustomer;
    }

    @Override
    public Optional<PaymentGatewayCustomer> getByCustomerId(String customerId) {
        log.info("Retrieving Payment Gateway Customer by customer id {}", customerId);
        return paymentGatewayCustomerRepository.findByCustomerId(customerId);
    }

}
