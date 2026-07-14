package com.regadas.refereehub.service;

import com.regadas.refereehub.domain.Match;
import com.regadas.refereehub.domain.MatchStatus;
import com.regadas.refereehub.domain.Payment;
import com.regadas.refereehub.dto.CreateMatchRequest;
import com.regadas.refereehub.dto.MatchResponse;
import com.regadas.refereehub.dto.PaymentSummaryResponse;
import com.regadas.refereehub.dto.UpdateMatchRequest;
import com.regadas.refereehub.exception.MatchNotFoundException;
import com.regadas.refereehub.repository.MatchRepository;
import com.regadas.refereehub.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import com.regadas.refereehub.exception.InvalidDateRangeException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final PaymentRepository paymentRepository;

    public MatchService(
            MatchRepository matchRepository,
            PaymentRepository paymentRepository
    ) {
        this.matchRepository = matchRepository;
        this.paymentRepository = paymentRepository;
    }

    public List<MatchResponse> findAll(
            MatchStatus status,
            LocalDate startDate,
            LocalDate endDate
    ) {

        validateDateRange(startDate, endDate);

        List<Match> matches;

        boolean hasDateFilter = startDate != null && endDate != null;

        if (status != null && hasDateFilter) {
            matches = matchRepository.findByStatusAndDateBetween(status, startDate, endDate);

        } else if (status != null) {
            matches = matchRepository.findByStatus(status);

        } else if (hasDateFilter) {
            matches = matchRepository.findByDateBetween(startDate, endDate);

        } else {
            matches = matchRepository.findAll();
        }

        return matches.stream()
                .map(this::toResponse)
                .toList();
    }

    public MatchResponse findById(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new MatchNotFoundException(id));

        return toResponse(match);
    }

    public MatchResponse create(CreateMatchRequest request) {
        Match match = new Match();

        match.setDate(request.date());
        match.setTime(request.time());
        match.setRole(request.role());
        match.setAgeGroup(request.ageGroup());
        match.setDivision(request.division());
        match.setHomeTeam(request.homeTeam());
        match.setAwayTeam(request.awayTeam());
        match.setVenue(request.venue());
        match.setStatus(request.status());

        Match savedMatch = matchRepository.save(match);

        return toResponse(savedMatch);
    }

    public MatchResponse update(Long id, UpdateMatchRequest request) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new MatchNotFoundException(id));

        match.setDate(request.date());
        match.setTime(request.time());
        match.setRole(request.role());
        match.setAgeGroup(request.ageGroup());
        match.setDivision(request.division());
        match.setHomeTeam(request.homeTeam());
        match.setAwayTeam(request.awayTeam());
        match.setVenue(request.venue());
        match.setStatus(request.status());

        Match updatedMatch = matchRepository.save(match);

        return toResponse(updatedMatch);
    }

    public void delete(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new MatchNotFoundException(id));

        matchRepository.delete(match);
    }

    private MatchResponse toResponse(Match match) {
        PaymentSummaryResponse paymentSummary = paymentRepository.findByMatchId(match.getId())
                .map(this::toPaymentSummary)
                .orElse(null);

        return new MatchResponse(
                match.getId(),
                match.getDate(),
                match.getTime(),
                match.getRole(),
                match.getAgeGroup(),
                match.getDivision(),
                match.getHomeTeam(),
                match.getAwayTeam(),
                match.getVenue(),
                match.getStatus().name(),
                paymentSummary
        );
    }

    private PaymentSummaryResponse toPaymentSummary(Payment payment) {
        return new PaymentSummaryResponse(
                payment.getId(),
                payment.getFeeAmount(),
                calculateMileageAmount(payment),
                payment.isNightSubsidyApplied(),
                payment.getNightSubsidyAmount(),
                calculateTotalAmount(payment),
                payment.isPaid()
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
}