package com.omarahmed42.payment.service.impl;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.omarahmed42.payment.dto.message.Message;
import com.omarahmed42.payment.dto.request.PaymentCardRequest;
import com.omarahmed42.payment.dto.request.PaymentRequest;
import com.omarahmed42.payment.dto.response.PaymentResponse;
import com.omarahmed42.payment.dto.response.SetupIntentResponse;
import com.omarahmed42.payment.enums.PaymentGatewayType;
import com.omarahmed42.payment.exception.PaymentNotFoundException;
import com.omarahmed42.payment.exception.PaymentOrderNotFoundException;
import com.omarahmed42.payment.mapper.PaymentMapper;
import com.omarahmed42.payment.message.payload.PaymentFailedPayload;
import com.omarahmed42.payment.message.payload.RetrievePaymentPayload;
import com.omarahmed42.payment.message.producer.MessageSender;
import com.omarahmed42.payment.model.Payment;
import com.omarahmed42.payment.model.PaymentGatewayCustomer;
import com.omarahmed42.payment.model.PaymentOrder;
import com.omarahmed42.payment.repository.PaymentGatewayCustomerRepository;
import com.omarahmed42.payment.repository.PaymentOrderRepository;
import com.omarahmed42.payment.repository.PaymentRepository;
import com.omarahmed42.payment.service.PaymentGatewayCustomerService;
import com.omarahmed42.payment.service.PaymentService;
import com.omarahmed42.payment.utils.SecurityUtils;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethod.Card;
import com.stripe.model.SetupIntent;
import com.stripe.model.StripeObject;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.PaymentMethodCreateParams;
import com.stripe.param.SetupIntentCreateParams;
import com.stripe.param.SetupIntentCreateParams.Usage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentOrderRepository paymentOrderRepository;
    private final PaymentGatewayCustomerRepository paymentGatewayCustomerRepository;
    private final PaymentMapper paymentMapper;
    private final MessageSender messageSender;
    private final PaymentGatewayCustomerService paymentGatewayCustomerService;

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long paymentId) {
        Payment payment = paymentRepository.findOne(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment with id " + paymentId + " not found"));
        return paymentMapper.toPaymentResponse(payment);
    }

    @Override
    public void chargeCard(PaymentRequest paymentRequest) {
        Long paymentId = paymentRequest.getPaymentId();
        Payment payment = paymentRepository.findOne(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment with id " + paymentId + " not found"));

        String userId = paymentRequest.getUserId();
        if (!payment.getUserId().equals(userId))
            throw new RuntimeException("Unauthorized access");

        PaymentGatewayCustomer gatewayCustomer = payment.getGatewayCustomer();
        String customerId = gatewayCustomer.getCustomerId();

        BigDecimal totalCost = paymentRequest.getTotalCost();
        PaymentIntentCreateParams params = new PaymentIntentCreateParams.Builder()
                .setCustomer(customerId)
                .setPaymentMethod(payment.getCardId())
                .setConfirm(true)
                .setCurrency("usd")
                .putMetadata("orderId", paymentRequest.getOrderId().toString())
                .setAmount(totalCost.multiply(BigDecimal.valueOf(100)).longValue()).build();

        try {
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            PaymentOrder paymentOrder = new PaymentOrder();
            paymentOrder.setPaymentIntentId(paymentIntent.getId());
            paymentOrder.setCreatedAt(paymentIntent.getCreated());
            paymentOrder.setStatus(paymentIntent.getStatus());
            paymentOrder.setOrderId(paymentRequest.getOrderId());

            paymentOrderRepository.save(paymentOrder);
        } catch (StripeException e) {
            log.error("Error while charging card {}", e);
        }
    }

    @Override
    public void handlePaymentIntent(String eventType, PaymentIntent paymentIntent) {
        switch (eventType) {
            case "payment_intent.succeeded":
                log.info("Payment for {} succeeded.", paymentIntent.getAmount());
                handlePaymentIntentSucceeded(paymentIntent);
                break;
            case "payment_intent.payment_failed":
                log.info("Payment for {} failed.", paymentIntent.getId());
                handlePaymentIntentFailed(paymentIntent);
                break;
            default:
                log.warn("Unhandled event type: {}", eventType);
                break;
        }

    }

    private void handlePaymentIntentSucceeded(PaymentIntent paymentIntent) {
        PaymentOrder paymentOrder = paymentOrderRepository.findByPaymentIntentId(paymentIntent.getId())
                .orElseThrow(PaymentOrderNotFoundException::new);

        paymentOrder.setStatus(paymentIntent.getStatus());
        paymentOrderRepository.save(paymentOrder);

        String correlationId = UUID.randomUUID().toString();
        RetrievePaymentPayload payload = new RetrievePaymentPayload();
        payload.setPaymentId(null);
        payload.setOrderId(paymentOrder.getOrderId());
        payload.setCorrelationId(correlationId);

        Message<RetrievePaymentPayload> message = new Message<>("PaymentRetrievedEvent", payload);
        message.setCorrelationId(correlationId);
        messageSender.send(message);
    }

    private void handlePaymentIntentFailed(PaymentIntent paymentIntent) {
        PaymentOrder paymentOrder = paymentOrderRepository.findByPaymentIntentId(paymentIntent.getId())
                .orElseThrow(PaymentOrderNotFoundException::new);

        paymentOrder.setStatus(paymentIntent.getStatus());
        paymentOrderRepository.save(paymentOrder);

        String correlationId = UUID.randomUUID().toString();
        PaymentFailedPayload payload = new PaymentFailedPayload();
        payload.setOrderId(paymentOrder.getOrderId());
        payload.setPaymentIntentId(paymentIntent.getId());
        payload.setCorrelationId(correlationId);

        Message<PaymentFailedPayload> message = new Message<>("PaymentFailedEvent", payload);
        message.setCorrelationId(correlationId);
    }

    @Override
    public void addPayment(PaymentCardRequest paymentRequest) {
        try {
            String userId = SecurityUtils.getSubject();
            Optional<PaymentGatewayCustomer> maybeGatewayCustomer = paymentGatewayCustomerRepository
                    .findByUserId(userId);

            PaymentGatewayCustomer paymentGatewayCustomer = null;
            if (maybeGatewayCustomer.isEmpty()) {
                Customer customer = Customer
                        .create(CustomerCreateParams.builder()
                                .setName(userId)
                                .setMetadata(Map.of("userId", userId)).build());

                paymentGatewayCustomer = new PaymentGatewayCustomer();
                paymentGatewayCustomer.setType(PaymentGatewayType.STRIPE);
                paymentGatewayCustomer.setUserId(userId);
                paymentGatewayCustomer.setCustomerId(customer.getId());
                paymentGatewayCustomer = paymentGatewayCustomerRepository.save(paymentGatewayCustomer);
            } else {
                paymentGatewayCustomer = maybeGatewayCustomer.get();
            }

            PaymentMethodCreateParams params = PaymentMethodCreateParams
                    .builder()
                    .setType(PaymentMethodCreateParams.Type.CARD)
                    .setCard(buildCardDetails(paymentRequest))
                    .build();

            PaymentMethod paymentMethod = PaymentMethod.create(params);
            PaymentMethodAttachParams paymentMethodAttachParams = PaymentMethodAttachParams.builder()
                    .setCustomer(paymentGatewayCustomer.getCustomerId()).build();
            paymentMethod = paymentMethod.attach(paymentMethodAttachParams);

            Payment payment = new Payment();
            payment.setCardId(paymentMethod.getId());
            payment.setUserId(userId);
            payment.setGatewayCustomer(paymentGatewayCustomer);
            payment.setLast4(paymentMethod.getCard().getLast4());
            payment.setExpYear(paymentRequest.expYear().intValue());
            payment.setExpMonth(paymentRequest.expMonth().shortValue());

            payment = paymentRepository.save(payment);
            log.debug("Payment id {}", payment.getId().toString());
            // return payment.getId();
        } catch (StripeException e) {
            log.error("Stripe exception {}", e);
        }
    }

    private PaymentMethodCreateParams.CardDetails buildCardDetails(PaymentCardRequest payment) {
        return PaymentMethodCreateParams.CardDetails
                .builder()
                .setNumber(payment.number())
                .setExpYear(payment.expYear())
                .setExpMonth(payment.expMonth())
                .setCvc(payment.cvc())
                .build();
    }

    @Override
    public SetupIntentResponse setupIntent() throws StripeException {
        String userId = SecurityUtils.getSubject();
        PaymentGatewayCustomer gatewayCustomer = paymentGatewayCustomerService.getOrAddPaymentGatewayCustomer(userId);
        SetupIntentCreateParams params = SetupIntentCreateParams.builder()
                .setCustomer(gatewayCustomer.getCustomerId())
                .addPaymentMethodType("card")
                // .setAutomaticPaymentMethods(
                // SetupIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build())
                .setUsage(Usage.OFF_SESSION)
                .build();

        SetupIntent setupIntent = SetupIntent.create(params);
        return new SetupIntentResponse(setupIntent.getClientSecret());
    }

    @Override
    public void handleEvent(String eventType, StripeObject stripeObject) {
        switch (eventType) {
            case "payment_method.attached":
                log.info("Payment method with id {} attached for customer with id {}",
                        ((PaymentMethod) stripeObject).getId(), ((PaymentMethod) stripeObject).getCustomer());
                handlePaymentMethodAttached((PaymentMethod) stripeObject);
                break;
            case "payment_intent.succeeded":
                log.info("Payment for {} succeeded.", ((PaymentIntent) stripeObject).getAmount());
                handlePaymentIntentSucceeded((PaymentIntent) stripeObject);
                break;
            case "payment_intent.payment_failed":
                log.info("Payment for {} failed.", ((PaymentIntent) stripeObject).getId());
                handlePaymentIntentFailed((PaymentIntent) stripeObject);
                break;
            default:
                log.warn("Unhandled event type: {}", eventType);
                break;
        }
    }

    private void handlePaymentMethodAttached(PaymentMethod paymentMethod) {
        Card card = paymentMethod.getCard();
        PaymentGatewayCustomer gatewayCustomer = paymentGatewayCustomerService
                .getByCustomerId(paymentMethod.getCustomer()).orElseThrow();
        String userId = gatewayCustomer.getUserId();

        Payment payment = new Payment();
        payment.setCardId(paymentMethod.getId());
        payment.setUserId(userId);
        payment.setGatewayCustomer(gatewayCustomer);

        payment.setLast4(paymentMethod.getCard().getLast4());
        payment.setExpYear(card.getExpYear().intValue());
        payment.setExpMonth(card.getExpMonth().shortValue());
        paymentRepository.save(payment);
    }

}
