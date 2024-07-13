package com.omarahmed42.payment.service;

import java.util.UUID;

import com.omarahmed42.payment.dto.request.PaymentCardRequest;
import com.omarahmed42.payment.dto.request.PaymentRequest;
import com.omarahmed42.payment.dto.response.PaymentResponse;
import com.omarahmed42.payment.dto.response.SetupIntentResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;

public interface PaymentService {
    PaymentResponse getPayment(UUID paymentId);

    void chargeCard(PaymentRequest paymentRequest, String correlationId);
    void chargeCard(PaymentRequest paymentRequest);

    void handlePaymentIntent(String type, PaymentIntent stripeObject);

    void addPayment(PaymentCardRequest payment);

    SetupIntentResponse setupIntent() throws StripeException;

    void handleEvent(String type, StripeObject stripeObject);
}
