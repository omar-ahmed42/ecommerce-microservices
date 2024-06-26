package com.omarahmed42.payment.dto.request;

public record PaymentCardRequest(String number, Long expYear, Long expMonth, String cvc) {
    
}
