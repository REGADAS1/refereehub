package com.regadas.refereehub.service;

import com.regadas.refereehub.domain.Match;
import com.regadas.refereehub.domain.Payment;
import com.regadas.refereehub.dto.CreatePaymentRequest;
import com.regadas.refereehub.dto.PaymentResponse;
import com.regadas.refereehub.dto.UpdatePaymentRequest;
import com.regadas.refereehub.exception.MatchNotFoundException;
import com.regadas.refereehub.exception.PaymentAlreadyExistsException;
import com.regadas.refereehub.repository.MatchRepository;
import com.regadas.refereehub.repository.PaymentRepository;
import com.regadas.refereehub.exception.PaymentNotFoundException;
import com.regadas.refereehub.exception.PaymentNotFoundForMatchException;
import com.regadas.refereehub.exception.InvalidPaymentRequestException;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    private static final BigDecimal NIGHT_SUBSIDY_AMOUNT = new BigDecimal("13.00");

    private final PaymentRepository paymentRepository;
    private final MatchRepository matchRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            MatchRepository matchRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.matchRepository = matchRepository;
    }

    public PaymentResponse create(Long matchId, CreatePaymentRequest request) {
        validatePaymentRequest(
                request.paid(),
                request.paidAt(),
                request.kilometers(),
                request.kmRate()
        );

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException(matchId));

        if (paymentRepository.existsByMatchId(matchId)) {
            throw new PaymentAlreadyExistsException(matchId);
        }

        Payment payment = new Payment();
        payment.setMatch(match);
        payment.setFeeAmount(request.feeAmount());
        payment.setPaid(request.paid());
        payment.setPaidAt(request.paidAt());
        payment.setKilometers(request.kilometers());
        payment.setKmRate(request.kmRate());
        payment.setNightSubsidyApplied(request.nightSubsidyApplied());
        payment.setNightSubsidyAmount(calculateNightSubsidyAmount(request.nightSubsidyApplied()));
        payment.setNotes(request.notes());

        Payment savedPayment = paymentRepository.save(payment);

        return toResponse(savedPayment);
    }

    public List<PaymentResponse> findPending() {
        return paymentRepository.findByPaidFalse()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private PaymentResponse toResponse(Payment payment) {
        BigDecimal mileageAmount = calculateMileageAmount(payment);
        BigDecimal totalAmount = calculateTotalAmount(payment);

        return new PaymentResponse(
                payment.getId(),
                payment.getMatch().getId(),
                payment.getFeeAmount(),
                payment.isPaid(),
                payment.getPaidAt(),
                payment.getKilometers(),
                payment.getKmRate(),
                mileageAmount,
                payment.isNightSubsidyApplied(),
                payment.getNightSubsidyAmount(),
                totalAmount,
                payment.getNotes()
        );
    }

    private BigDecimal calculateNightSubsidyAmount(boolean nightSubsidyApplied) {
        if (nightSubsidyApplied) {
            return NIGHT_SUBSIDY_AMOUNT;
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal calculateMileageAmount(Payment payment) {
        BigDecimal kilometers = valueOrZero(payment.getKilometers());
        BigDecimal kmRate = valueOrZero(payment.getKmRate());

        return kilometers.multiply(kmRate);
    }

    private BigDecimal calculateTotalAmount(Payment payment) {
        BigDecimal feeAmount = valueOrZero(payment.getFeeAmount());
        BigDecimal mileageAmount = calculateMileageAmount(payment);
        BigDecimal nightSubsidyAmount = valueOrZero(payment.getNightSubsidyAmount());

        return feeAmount
                .add(mileageAmount)
                .add(nightSubsidyAmount);
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }


    public PaymentResponse findByMatchId(Long matchId) {
        if (!matchRepository.existsById(matchId)) {
            throw new MatchNotFoundException(matchId);
        }

        Payment payment = paymentRepository.findByMatchId(matchId)
                .orElseThrow(() -> new PaymentNotFoundForMatchException(matchId));

        return toResponse(payment);
    }

    public PaymentResponse update(Long id, UpdatePaymentRequest request) {

        validatePaymentRequest(
                request.paid(),
                request.paidAt(),
                request.kilometers(),
                request.kmRate()
        );

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        payment.setFeeAmount(request.feeAmount());
        payment.setPaid(request.paid());
        payment.setPaidAt(request.paidAt());
        payment.setKilometers(request.kilometers());
        payment.setKmRate(request.kmRate());
        payment.setNightSubsidyApplied(request.nightSubsidyApplied());
        payment.setNightSubsidyAmount(calculateNightSubsidyAmount(request.nightSubsidyApplied()));
        payment.setNotes(request.notes());

        Payment updatedPayment = paymentRepository.save(payment);

        return toResponse(updatedPayment);
    }

    private void validatePaymentRequest(
            boolean paid,
            LocalDate paidAt,
            BigDecimal kilometers,
            BigDecimal kmRate
    ) {
        if (paid && paidAt == null) {
            throw new InvalidPaymentRequestException("paidAt is required when paid is true.");
        }

        if (!paid && paidAt != null) {
            throw new InvalidPaymentRequestException("paidAt must be null when paid is false.");
        }

        boolean hasKilometers = kilometers != null;
        boolean hasKmRate = kmRate != null;

        if (hasKilometers != hasKmRate) {
            throw new InvalidPaymentRequestException("kilometers and kmRate must be provided together.");
        }
    }
}