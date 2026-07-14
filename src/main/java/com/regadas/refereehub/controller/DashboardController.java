package com.regadas.refereehub.controller;

import com.regadas.refereehub.dto.DashboardSummaryResponse;
import com.regadas.refereehub.service.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.regadas.refereehub.dto.DashboardCategoryCountResponse;
import com.regadas.refereehub.dto.DashboardMonthlyEarningsResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/api/dashboard/summary")
    public DashboardSummaryResponse getSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return dashboardService.getSummary(startDate, endDate);
    }

    @GetMapping("/api/dashboard/matches-by-role")
    public List<DashboardCategoryCountResponse> getMatchesByRole(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return dashboardService.getMatchesByRole(startDate, endDate);
    }

    @GetMapping("/api/dashboard/matches-by-age-group")
    public List<DashboardCategoryCountResponse> getMatchesByAgeGroup(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return dashboardService.getMatchesByAgeGroup(startDate, endDate);
    }

    @GetMapping("/api/dashboard/earnings-by-month")
    public List<DashboardMonthlyEarningsResponse> getEarningsByMonth(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return dashboardService.getEarningsByMonth(startDate, endDate);
    }
}