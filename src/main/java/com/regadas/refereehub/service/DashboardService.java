package com.regadas.refereehub.service;

import com.regadas.refereehub.domain.Match;
import com.regadas.refereehub.domain.MatchStatus;
import com.regadas.refereehub.domain.Payment;
import com.regadas.refereehub.dto.DashboardSummaryResponse;
import com.regadas.refereehub.repository.MatchRepository;
import com.regadas.refereehub.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import com.regadas.refereehub.exception.InvalidDateRangeException;
import com.regadas.refereehub.dto.DashboardCategoryCountResponse;
import com.regadas.refereehub.dto.DashboardMonthlyEarningsResponse;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {

    private final MatchRepository matchRepository;
    private final PaymentRepository paymentRepository;

    public DashboardService(
            MatchRepository matchRepository,
            PaymentRepository paymentRepository) {
        this.matchRepository = matchRepository;
        this.paymentRepository = paymentRepository;
    }

    public DashboardSummaryResponse getSummary(LocalDate startDate, LocalDate endDate) {

        validateDateRange(startDate, endDate);
        List<Match> matches = findMatches(startDate, endDate);

        List<Long> matchIds = matches.stream()
                .map(Match::getId)
                .toList();

        List<Payment> payments = matchIds.isEmpty()
                ? List.of()
                : paymentRepository.findByMatchIdIn(matchIds);

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
                totalKilometers);
    }

    private List<Match> findMatches(LocalDate startDate, LocalDate endDate) {
        boolean hasDateFilter = startDate != null && endDate != null;

        if (hasDateFilter) {
            return matchRepository.findByDateBetween(startDate, endDate);
        }

        return matchRepository.findAll();
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

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return;
        }

        if (startDate == null || endDate == null) {
            throw new InvalidDateRangeException("startDate and endDate must be provided together.");
        }

        if (endDate.isBefore(startDate)) {
            throw new InvalidDateRangeException("endDate cannot be before startDate.");
        }
    }

    public List<DashboardCategoryCountResponse> getMatchesByRole(
            LocalDate startDate,
            LocalDate endDate) {
        validateDateRange(startDate, endDate);

        List<Match> matches = findMatches(startDate, endDate);

        return matches.stream()
                .collect(Collectors.groupingBy(
                        Match::getRole,
                        Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> new DashboardCategoryCountResponse(
                        entry.getKey(),
                        entry.getValue()))
                .sorted(Comparator.comparing(DashboardCategoryCountResponse::count).reversed())
                .toList();
    }

    public List<DashboardCategoryCountResponse> getMatchesByAgeGroup(
            LocalDate startDate,
            LocalDate endDate) {
        validateDateRange(startDate, endDate);

        List<Match> matches = findMatches(startDate, endDate);

        return matches.stream()
                .filter(match -> match.getAgeGroup() != null && !match.getAgeGroup().isBlank())
                .collect(Collectors.groupingBy(
                        Match::getAgeGroup,
                        Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> new DashboardCategoryCountResponse(
                        entry.getKey(),
                        entry.getValue()))
                .sorted(Comparator.comparing(DashboardCategoryCountResponse::count).reversed())
                .toList();
    }

    public List<DashboardMonthlyEarningsResponse> getEarningsByMonth(
                LocalDate startDate,
                LocalDate endDate
        ) {
        validateDateRange(startDate, endDate);

        List<Match> matches = findMatches(startDate, endDate);

        List<Long> matchIds = matches.stream()
                .map(Match::getId)
                .toList();

        List<Payment> payments = matchIds.isEmpty()
                ? List.of()
                : paymentRepository.findByMatchIdIn(matchIds);

        Map<Long, Match> matchesById = matches.stream()
                .collect(Collectors.toMap(
                        Match::getId,
                        match -> match
                ));

        Map<YearMonth, List<Payment>> paymentsByMonth = payments.stream()
                .collect(Collectors.groupingBy(payment -> {
                        Match match = matchesById.get(payment.getMatch().getId());
                        return YearMonth.from(match.getDate());
                }));

        return paymentsByMonth.entrySet()
                .stream()
                .map(entry -> {
                        YearMonth month = entry.getKey();
                        List<Payment> monthPayments = entry.getValue();

                        BigDecimal totalEarned = monthPayments.stream()
                                .map(this::calculateTotalAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal totalReceived = monthPayments.stream()
                                .filter(Payment::isPaid)
                                .map(this::calculateTotalAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal totalPending = monthPayments.stream()
                                .filter(payment -> !payment.isPaid())
                                .map(this::calculateTotalAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        return new DashboardMonthlyEarningsResponse(
                                month.toString(),
                                totalEarned,
                                totalReceived,
                                totalPending
                        );
                })
                .sorted(Comparator.comparing(DashboardMonthlyEarningsResponse::month))
                .toList();
        }
}