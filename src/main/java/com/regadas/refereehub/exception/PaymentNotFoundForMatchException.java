package com.regadas.refereehub.exception;

public class PaymentNotFoundForMatchException extends RuntimeException {

    public PaymentNotFoundForMatchException(Long matchId) {
        super("Payment not found for match with id: " + matchId);
    }
}