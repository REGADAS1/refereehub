package com.regadas.refereehub.exception;

public class PaymentAlreadyExistsException extends RuntimeException {

    public PaymentAlreadyExistsException(Long matchId) {
        super("Payment already exists for match with id: " + matchId);
    }
}