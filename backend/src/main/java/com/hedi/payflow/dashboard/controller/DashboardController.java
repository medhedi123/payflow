package com.hedi.payflow.dashboard.controller;

import com.hedi.payflow.dashboard.dto.DashboardSummaryResponse;
import com.hedi.payflow.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.hedi.payflow.dashboard.dto.MerchantDashboardResponse;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary(Authentication authentication) {
        return dashboardService.getSummary(authentication);
    }
    @GetMapping("/merchant")
    public MerchantDashboardResponse getMerchantDashboard(Authentication authentication) {
        return dashboardService.getMerchantDashboard(authentication);
    }
}