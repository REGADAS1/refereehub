package com.regadas.refereehub.dto;

import com.regadas.refereehub.domain.MatchStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateMatchRequest(
        @NotNull LocalDate date,
        LocalTime time,

        @NotBlank String role,

        String ageGroup,
        String division,

        @NotBlank String homeTeam,
        @NotBlank String awayTeam,

        String venue,

        @NotNull MatchStatus status
) {
}