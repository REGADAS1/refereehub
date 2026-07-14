package com.regadas.refereehub.dto;

import java.math.BigDecimal;

public record PaymentSummaryResponse(
        Long id,
        BigDecimal feeAmount,
        BigDecimal mileageAmount,
        boolean nightSubsidyApplied,
        BigDecimal nightSubsidyAmount,
        BigDecimal totalAmount,
        boolean paid
) {
}