package com.regadas.refereehub.exception;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(Long matchId) {
        super("Payment not found for match with id: " + matchId);
    }
}
