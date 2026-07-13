package com.regadas.refereehub.controller;

import com.regadas.refereehub.dto.CreatePaymentRequest;
import com.regadas.refereehub.dto.PaymentResponse;
import com.regadas.refereehub.dto.UpdatePaymentRequest;
import com.regadas.refereehub.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/api/matches/{matchId}/payment")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse createPayment(
            @PathVariable Long matchId,
            @Valid @RequestBody CreatePaymentRequest request
    ) {
        return paymentService.create(matchId, request);
    }

    @GetMapping("/api/payments/pending")
    public List<PaymentResponse> getPendingPayments() {
        return paymentService.findPending();
    }

    @GetMapping("/api/matches/{matchId}/payment")
    public PaymentResponse getPaymentByMatchId(@PathVariable Long matchId) {
        return paymentService.findByMatchId(matchId);
    }

    @PutMapping("/api/payments/{id}")
    public PaymentResponse updatePayment(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePaymentRequest request
    ) {
        return paymentService.update(id, request);
    }

    
}