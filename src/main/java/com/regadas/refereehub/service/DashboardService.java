package com.regadas.refereehub.service;

import com.regadas.refereehub.domain.Match;
import com.regadas.refereehub.domain.MatchStatus;
import com.regadas.refereehub.domain.Payment;
import com.regadas.refereehub.dto.DashboardSummaryResponse;
import com.regadas.refereehub.repository.MatchRepository;
import com.regadas.refereehub.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DashboardService {

    private final MatchRepository matchRepository;
    private final PaymentRepository paymentRepository;

    public DashboardService(
            MatchRepository matchRepository,
            PaymentRepository paymentRepository
    ) {
        this.matchRepository = matchRepository;
        this.paymentRepository = paymentRepository;
    }

    public DashboardSummaryResponse getSummary() {
        List<Match> matches = matchRepository.findAll();
        List<Payment> payments = paymentRepository.findAll();

        long totalMatches = matches.size();

        long scheduledMatches = matches.stream()
                .filter(match -> match.getStatus() == MatchStatus.SCHEDULED)
                .count();

        long completedMatches = matches.stream()
                .filter(match -> match.getStatus() == MatchStatus.COMPLETED)
                .count();

        BigDecimal totalEarned = payments.stream()
                .map(this::calculateTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalReceived = payments.stream()
                .filter(Payment::isPaid)
                .map(this::calculateTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPending = payments.stream()
                .filter(payment -> !payment.isPaid())
                .map(this::calculateTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pendingPayments = payments.stream()
                .filter(payment -> !payment.isPaid())
                .count();

        BigDecimal totalKilometers = payments.stream()
                .map(payment -> valueOrZero(payment.getKilometers()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardSummaryResponse(
                totalMatches,
                scheduledMatches,
                completedMatches,
                totalEarned,
                totalReceived,
                totalPending,
                pendingPayments,
                totalKilometers
        );
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
}