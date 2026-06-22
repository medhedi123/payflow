package com.hedi.payflow.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class DashboardSummaryResponse {

    private BigDecimal walletBalance;
    private BigDecimal totalDeposits;
    private BigDecimal totalTransfersSent;
    private BigDecimal totalTransfersReceived;
    private int transactionCount;
}