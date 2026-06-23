package com.hedi.payflow.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class MerchantDashboardResponse {

    private long totalInvoices;
    private long pendingInvoices;
    private long paidInvoices;
    private BigDecimal totalRevenue;
    private BigDecimal walletBalance;
}