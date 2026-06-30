package com.hedi.payflow.dashboard.dto;

import java.math.BigDecimal;

public record DashboardResponse(
        BigDecimal walletBalance,
        BigDecimal totalDepositsAmount,
        BigDecimal totalTransfersSentAmount,
        BigDecimal totalTransfersReceivedAmount,
        BigDecimal totalPaymentsAmount,
        BigDecimal totalReversalsAmount,

        long totalTransactions,
        long successfulTransactions,
        long pendingTransactions,
        long failedTransactions,
        long reversedTransactions,

        long totalInvoices,
        long paidInvoices,
        long pendingInvoices,
        BigDecimal invoiceRevenue
) {
}