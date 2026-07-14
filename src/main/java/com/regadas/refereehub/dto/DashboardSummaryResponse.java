package com.regadas.refereehub.dto;

import java.math.BigDecimal;

public record DashboardSummaryResponse(
        long totalMatches,
        long scheduledMatches,
        long completedMatches,
        BigDecimal totalEarned,
        BigDecimal totalReceived,
        BigDecimal totalPending,
        long pendingPayments,
        BigDecimal totalKilometers
) {
}