package com.regadas.refereehub.service;

import com.regadas.refereehub.domain.Match;
import com.regadas.refereehub.domain.MatchStatus;
import com.regadas.refereehub.domain.Payment;
import com.regadas.refereehub.dto.DashboardCategoryCountResponse;
import com.regadas.refereehub.dto.DashboardMonthlyEarningsResponse;
import com.regadas.refereehub.dto.DashboardSummaryResponse;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void shouldReturnDashboardSummary() {
        Match match1 = createMatch(1L, LocalDate.of(2026, 7, 6), "REFEREE", "Seniores", MatchStatus.SCHEDULED);
        Match match2 = createMatch(2L, LocalDate.of(2026, 7, 7), "REFEREE", "Juvenis", MatchStatus.SCHEDULED);

        Payment payment1 = createPayment(
                1L,
                match1,
                new BigDecimal("42.50"),
                false,
                new BigDecimal("28"),
                new BigDecimal("0.36"),
                true
        );

        Payment payment2 = createPayment(
                2L,
                match2,
                new BigDecimal("42.50"),
                true,
                new BigDecimal("28"),
                new BigDecimal("0.36"),
                true
        );

        when(matchRepository.findAll()).thenReturn(List.of(match1, match2));
        when(paymentRepository.findByMatchIdIn(anyList())).thenReturn(List.of(payment1, payment2));

        DashboardSummaryResponse response = dashboardService.getSummary(null, null);

        assertEquals(2, response.totalMatches());
        assertEquals(2, response.scheduledMatches());
        assertEquals(0, response.completedMatches());

        assertBigDecimalEquals("131.16", response.totalEarned());
        assertBigDecimalEquals("65.58", response.totalReceived());
        assertBigDecimalEquals("65.58", response.totalPending());

        assertEquals(1, response.pendingPayments());

        assertBigDecimalEquals("56.00", response.totalKilometers());

        verify(matchRepository).findAll();
        verify(paymentRepository).findByMatchIdIn(anyList());
    }

    @Test
    void shouldReturnDashboardSummaryFilteredByDate() {
        LocalDate startDate = LocalDate.of(2026, 7, 1);
        LocalDate endDate = LocalDate.of(2026, 7, 31);

        Match match = createMatch(1L, LocalDate.of(2026, 7, 6), "REFEREE", "Seniores", MatchStatus.COMPLETED);

        Payment payment = createPayment(
                1L,
                match,
                new BigDecimal("42.50"),
                true,
                new BigDecimal("28"),
                new BigDecimal("0.36"),
                true
        );

        when(matchRepository.findByDateBetween(startDate, endDate)).thenReturn(List.of(match));
        when(paymentRepository.findByMatchIdIn(anyList())).thenReturn(List.of(payment));

        DashboardSummaryResponse response = dashboardService.getSummary(startDate, endDate);

        assertEquals(1, response.totalMatches());
        assertEquals(0, response.scheduledMatches());
        assertEquals(1, response.completedMatches());

        assertBigDecimalEquals("65.58", response.totalEarned());
        assertBigDecimalEquals("65.58", response.totalReceived());
        assertBigDecimalEquals("0", response.totalPending());
        assertEquals(0, response.pendingPayments());

        verify(matchRepository).findByDateBetween(startDate, endDate);
    }

    @Test
    void shouldGroupMatchesByRole() {
        Match match1 = createMatch(1L, LocalDate.of(2026, 7, 6), "REFEREE", "Seniores", MatchStatus.SCHEDULED);
        Match match2 = createMatch(2L, LocalDate.of(2026, 7, 7), "REFEREE", "Juvenis", MatchStatus.SCHEDULED);
        Match match3 = createMatch(3L, LocalDate.of(2026, 7, 8), "ASSISTANT_REFEREE", "Juniores", MatchStatus.COMPLETED);

        when(matchRepository.findAll()).thenReturn(List.of(match1, match2, match3));

        List<DashboardCategoryCountResponse> response = dashboardService.getMatchesByRole(null, null);

        assertEquals(2, response.size());

        assertEquals("REFEREE", response.get(0).label());
        assertEquals(2, response.get(0).count());

        assertEquals("ASSISTANT_REFEREE", response.get(1).label());
        assertEquals(1, response.get(1).count());
    }

    @Test
    void shouldGroupMatchesByAgeGroup() {
        Match match1 = createMatch(1L, LocalDate.of(2026, 7, 6), "REFEREE", "Seniores", MatchStatus.SCHEDULED);
        Match match2 = createMatch(2L, LocalDate.of(2026, 7, 7), "REFEREE", "Seniores", MatchStatus.SCHEDULED);
        Match match3 = createMatch(3L, LocalDate.of(2026, 7, 8), "REFEREE", "Juvenis", MatchStatus.COMPLETED);

        when(matchRepository.findAll()).thenReturn(List.of(match1, match2, match3));

        List<DashboardCategoryCountResponse> response = dashboardService.getMatchesByAgeGroup(null, null);

        assertEquals(2, response.size());

        assertEquals("Seniores", response.get(0).label());
        assertEquals(2, response.get(0).count());

        assertEquals("Juvenis", response.get(1).label());
        assertEquals(1, response.get(1).count());
    }

    @Test
    void shouldReturnEarningsByMonth() {
        Match januaryMatch = createMatch(1L, LocalDate.of(2026, 1, 15), "REFEREE", "Seniores", MatchStatus.COMPLETED);
        Match februaryMatch = createMatch(2L, LocalDate.of(2026, 2, 10), "REFEREE", "Juvenis", MatchStatus.COMPLETED);

        Payment januaryPayment = createPayment(
                1L,
                januaryMatch,
                new BigDecimal("42.50"),
                true,
                new BigDecimal("28"),
                new BigDecimal("0.36"),
                true
        );

        Payment februaryPayment = createPayment(
                2L,
                februaryMatch,
                new BigDecimal("50.00"),
                false,
                new BigDecimal("20"),
                new BigDecimal("0.36"),
                false
        );

        when(matchRepository.findAll()).thenReturn(List.of(januaryMatch, februaryMatch));
        when(paymentRepository.findByMatchIdIn(anyList())).thenReturn(List.of(januaryPayment, februaryPayment));

        List<DashboardMonthlyEarningsResponse> response = dashboardService.getEarningsByMonth(null, null);

        assertEquals(2, response.size());

        DashboardMonthlyEarningsResponse january = response.get(0);
        assertEquals("2026-01", january.month());
        assertBigDecimalEquals("65.58", january.totalEarned());
        assertBigDecimalEquals("65.58", january.totalReceived());
        assertBigDecimalEquals("0", january.totalPending());

        DashboardMonthlyEarningsResponse february = response.get(1);
        assertEquals("2026-02", february.month());
        assertBigDecimalEquals("57.20", february.totalEarned());
        assertBigDecimalEquals("0", february.totalReceived());
        assertBigDecimalEquals("57.20", february.totalPending());
    }

    private Match createMatch(
            Long id,
            LocalDate date,
            String role,
            String ageGroup,
            MatchStatus status
    ) {
        Match match = new Match();

        setId(match, id);

        match.setDate(date);
        match.setTime(LocalTime.of(16, 0));
        match.setRole(role);
        match.setAgeGroup(ageGroup);
        match.setDivision("Distrital");
        match.setHomeTeam("Equipa A");
        match.setAwayTeam("Equipa B");
        match.setVenue("Campo Municipal");
        match.setStatus(status);

        return match;
    }

    private Payment createPayment(
            Long id,
            Match match,
            BigDecimal feeAmount,
            boolean paid,
            BigDecimal kilometers,
            BigDecimal kmRate,
            boolean nightSubsidyApplied
    ) {
        Payment payment = new Payment();

        setId(payment, id);

        payment.setMatch(match);
        payment.setFeeAmount(feeAmount);
        payment.setPaid(paid);
        payment.setPaidAt(paid ? LocalDate.of(2026, 7, 13) : null);
        payment.setKilometers(kilometers);
        payment.setKmRate(kmRate);
        payment.setNightSubsidyApplied(nightSubsidyApplied);
        payment.setNightSubsidyAmount(nightSubsidyApplied ? new BigDecimal("13.00") : BigDecimal.ZERO);
        payment.setNotes("Teste");

        return payment;
    }

    private void setId(Object object, Long id) {
        try {
            var field = object.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(object, id);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private void assertBigDecimalEquals(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }
}