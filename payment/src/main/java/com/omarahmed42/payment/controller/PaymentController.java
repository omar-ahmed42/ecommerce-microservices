package com.omarahmed42.payment.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.shaded.gson.JsonSyntaxException;
import com.omarahmed42.payment.dto.request.PaymentCardRequest;
import com.omarahmed42.payment.dto.response.SetupIntentResponse;
import com.omarahmed42.payment.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/api/v1")
@RestController
@Slf4j
public class PaymentController {

    @Value("${stripe.endpoint.secret}")
    private String endpointSecret;

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payments")
    public ResponseEntity<Void> addPayment(@RequestBody PaymentCardRequest payment) {
        paymentService.addPayment(payment);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/payments/setup")
    public ResponseEntity<SetupIntentResponse> setupIntent() throws StripeException {
        return ResponseEntity.ok(paymentService.setupIntent());
    }

    @PostMapping("/payment-webhook")
    public ResponseEntity<String> listenToStripeEvents(@RequestHeader(name = "Stripe-Signature") String sigHeader,
            @RequestBody String payload) {

        Event event;
        if (endpointSecret != null && sigHeader != null) {
            // Only verify the event if you have an endpoint secret defined.
            // Otherwise, use the basic event deserialized with GSON.
            try {
                event = Webhook.constructEvent(
                        payload, sigHeader, endpointSecret);
            } catch (JsonSyntaxException e) {
                // Invalid payload
                log.error("⚠️  Webhook error while parsing basic request.");
                return ResponseEntity.badRequest().build();
            } catch (SignatureVerificationException e) {
                // Invalid signature
                log.error("⚠️  Webhook error while validating signature.");
                log.error("Signature error is {}", e);
                log.error("Code {} | Status Code {} | User Message {} | Message {} | SigHeader {}", e.getCode(), e.getStatusCode(), e.getUserMessage(), e.getMessage(), e.getSigHeader());
                return ResponseEntity.badRequest().build();
            }
            // Deserialize the nested object inside the event
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            log.info("Event version {} | Stripe version {}", event.getApiVersion(), Stripe.API_VERSION);
            StripeObject stripeObject;
            if (dataObjectDeserializer.getObject().isPresent()) {
                stripeObject = dataObjectDeserializer.getObject().get();
            } else {
                // Deserialization failed, probably due to an API version mismatch.
                // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
                // instructions on how to handle this case, or return an error here.
                log.error("Unable to deserialize event data object for {}", event);
                return ResponseEntity.badRequest().build();
            }
            // Handle the event
            paymentService.handleEvent(event.getType(), stripeObject);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
