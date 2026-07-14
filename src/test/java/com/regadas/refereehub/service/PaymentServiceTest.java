package com.regadas.refereehub.service;

import com.regadas.refereehub.domain.Match;
import com.regadas.refereehub.domain.MatchStatus;
import com.regadas.refereehub.domain.Payment;
import com.regadas.refereehub.dto.CreatePaymentRequest;
import com.regadas.refereehub.dto.PaymentResponse;
import com.regadas.refereehub.exception.InvalidPaymentRequestException;
import com.regadas.refereehub.repository.MatchRepository;
import com.regadas.refereehub.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void shouldCreatePaymentWithNightSubsidyAndCalculateTotals() {
        Long matchId = 1L;

        Match match = createMatch(matchId);

        CreatePaymentRequest request = new CreatePaymentRequest(
                new BigDecimal("42.50"),
                true,
                LocalDate.of(2026, 7, 13),
                new BigDecimal("28"),
                new BigDecimal("0.36"),
                true,
                "Pagamento recebido"
        );

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(paymentRepository.existsByMatchId(matchId)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            setPaymentId(payment, 10L);
            return payment;
        });

        PaymentResponse response = paymentService.create(matchId, request);

        assertEquals(10L, response.id());
        assertEquals(matchId, response.matchId());
        assertEquals(new BigDecimal("42.50"), response.feeAmount());
        assertTrue(response.paid());
        assertEquals(LocalDate.of(2026, 7, 13), response.paidAt());
        assertEquals(new BigDecimal("10.08"), response.mileageAmount());
        assertTrue(response.nightSubsidyApplied());
        assertEquals(new BigDecimal("13.00"), response.nightSubsidyAmount());
        assertEquals(new BigDecimal("65.58"), response.totalAmount());

        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void shouldRejectPaidPaymentWithoutPaidAt() {
        CreatePaymentRequest request = new CreatePaymentRequest(
                new BigDecimal("42.50"),
                true,
                null,
                new BigDecimal("28"),
                new BigDecimal("0.36"),
                true,
                "Inválido"
        );

        InvalidPaymentRequestException exception = assertThrows(
                InvalidPaymentRequestException.class,
                () -> paymentService.create(1L, request)
        );

        assertEquals("paidAt is required when paid is true.", exception.getMessage());

        verifyNoInteractions(matchRepository);
        verifyNoInteractions(paymentRepository);
    }

    @Test
    void shouldRejectUnpaidPaymentWithPaidAt() {
        CreatePaymentRequest request = new CreatePaymentRequest(
                new BigDecimal("42.50"),
                false,
                LocalDate.of(2026, 7, 13),
                new BigDecimal("28"),
                new BigDecimal("0.36"),
                true,
                "Inválido"
        );

        InvalidPaymentRequestException exception = assertThrows(
                InvalidPaymentRequestException.class,
                () -> paymentService.create(1L, request)
        );

        assertEquals("paidAt must be null when paid is false.", exception.getMessage());

        verifyNoInteractions(matchRepository);
        verifyNoInteractions(paymentRepository);
    }

    @Test
    void shouldRejectKilometersWithoutKmRate() {
        CreatePaymentRequest request = new CreatePaymentRequest(
                new BigDecimal("42.50"),
                false,
                null,
                new BigDecimal("28"),
                null,
                true,
                "Inválido"
        );

        InvalidPaymentRequestException exception = assertThrows(
                InvalidPaymentRequestException.class,
                () -> paymentService.create(1L, request)
        );

        assertEquals("kilometers and kmRate must be provided together.", exception.getMessage());

        verifyNoInteractions(matchRepository);
        verifyNoInteractions(paymentRepository);
    }

    private Match createMatch(Long id) {
        Match match = new Match();

        setMatchId(match, id);

        match.setDate(LocalDate.of(2026, 7, 6));
        match.setTime(LocalTime.of(16, 0));
        match.setRole("REFEREE");
        match.setAgeGroup("Seniores");
        match.setDivision("Distrital");
        match.setHomeTeam("Águias Alvelos");
        match.setAwayTeam("SC Cabreiros");
        match.setVenue("Campo Novo");
        match.setStatus(MatchStatus.SCHEDULED);

        return match;
    }

    private void setMatchId(Match match, Long id) {
        try {
            var field = Match.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(match, id);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private void setPaymentId(Payment payment, Long id) {
        try {
            var field = Payment.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(payment, id);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}