package com.regadas.refereehub.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreatePaymentRequest(
        @NotNull(message = "feeAmount is required")
        @DecimalMin(value = "0.01", message = "feeAmount must be greater than zero")
        BigDecimal feeAmount,

        boolean paid,

        LocalDate paidAt,

        @DecimalMin(value = "0.00", message = "kilometers cannot be negative")
        BigDecimal kilometers,

        @DecimalMin(value = "0.00", message = "kmRate cannot be negative")
        BigDecimal kmRate,

        boolean nightSubsidyApplied,

        String notes
) {
}