package com.hedi.payflow.dashboard.controller;

import com.hedi.payflow.dashboard.dto.DashboardSummaryResponse;
import com.hedi.payflow.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.hedi.payflow.dashboard.dto.MerchantDashboardResponse;
import com.hedi.payflow.dashboard.dto.DashboardResponse;
import com.hedi.payflow.dashboard.dto.DashboardChartsResponse;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary(Authentication authentication) {
        return dashboardService.getSummary(authentication);
    }
    @GetMapping("/analytics")
    public DashboardResponse getAnalytics(Authentication authentication) {
        return dashboardService.getAnalytics(authentication);
    }
    @GetMapping("/charts")
    public DashboardChartsResponse getCharts(Authentication authentication) {
        return dashboardService.getCharts(authentication);
    }
    @GetMapping("/merchant")
    public MerchantDashboardResponse getMerchantDashboard(Authentication authentication) {
        return dashboardService.getMerchantDashboard(authentication);
    }
}