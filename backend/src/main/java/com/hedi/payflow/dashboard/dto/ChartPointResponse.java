package com.hedi.payflow.dashboard.dto;

import java.math.BigDecimal;

public record ChartPointResponse(
        String label,
        BigDecimal value
) {
}