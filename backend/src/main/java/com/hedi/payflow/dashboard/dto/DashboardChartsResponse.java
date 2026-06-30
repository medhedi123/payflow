package com.hedi.payflow.dashboard.dto;

import java.util.List;

public record DashboardChartsResponse(
        List<ChartPointResponse> transactionVolume,
        List<ChartPointResponse> transactionTypes
) {
}