package com.regadas.refereehub.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdatePaymentRequest(
        @NotNull
        @DecimalMin("0.00")
        BigDecimal feeAmount,

        boolean paid,

        LocalDate paidAt,

        @DecimalMin("0.00")
        BigDecimal kilometers,

        @DecimalMin("0.00")
        BigDecimal kmRate,

        boolean nightSubsidyApplied,

        String notes
) {
}