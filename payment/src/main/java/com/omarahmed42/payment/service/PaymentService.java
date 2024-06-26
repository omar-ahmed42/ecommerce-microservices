package com.omarahmed42.payment.service;

import com.omarahmed42.payment.dto.request.PaymentCardRequest;
import com.omarahmed42.payment.dto.request.PaymentRequest;
import com.omarahmed42.payment.dto.response.PaymentResponse;
import com.stripe.model.PaymentIntent;

public interface PaymentService {
    PaymentResponse getPayment(Long paymentId);

    void chargeCard(PaymentRequest paymentRequest);

    void handlePaymentIntent(String type, PaymentIntent stripeObject);

    void addPayment(PaymentCardRequest payment);
}
