package com.regadas.refereehub.controller;

import com.regadas.refereehub.dto.DashboardSummaryResponse;
import com.regadas.refereehub.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/api/dashboard/summary")
    public DashboardSummaryResponse getSummary() {
        return dashboardService.getSummary();
    }
}