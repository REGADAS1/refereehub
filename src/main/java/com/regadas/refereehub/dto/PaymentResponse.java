package com.regadas.refereehub.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentResponse(
        Long id,
        Long matchId,
        BigDecimal feeAmount,
        boolean paid,
        LocalDate paidAt,
        BigDecimal kilometers,
        BigDecimal kmRate,
        BigDecimal mileageAmount,
        boolean nightSubsidyApplied,
        BigDecimal nightSubsidyAmount,
        BigDecimal totalAmount,
        String notes
) {
}