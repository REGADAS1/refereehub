package com.regadas.refereehub.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record MatchResponse(
        Long id,
        LocalDate date,
        LocalTime time,
        String role,
        String ageGroup,
        String division,
        String homeTeam,
        String awayTeam,
        String venue,
        String status,
        PaymentSummaryResponse paymentSummary
) 
{

}
