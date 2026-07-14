package com.regadas.refereehub.dto;

import com.regadas.refereehub.domain.MatchStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateMatchRequest(
        @NotNull(message = "date is required")
        LocalDate date,

        LocalTime time,

        @NotBlank(message = "role is required")
        String role,

        String ageGroup,

        String division,

        @NotBlank(message = "homeTeam is required")
        String homeTeam,

        @NotBlank(message = "awayTeam is required")
        String awayTeam,

        String venue,

        @NotNull(message = "status is required")
        MatchStatus status
) {
}