package com.regadas.refereehub.dto;

import java.math.BigDecimal;

public record DashboardMonthlyEarningsResponse(
        String month,
        BigDecimal totalEarned,
        BigDecimal totalReceived,
        BigDecimal totalPending
) {
}