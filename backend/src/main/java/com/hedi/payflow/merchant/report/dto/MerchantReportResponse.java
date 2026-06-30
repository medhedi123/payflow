package com.hedi.payflow.merchant.report.dto;

import java.math.BigDecimal;

public record MerchantReportResponse(
        String merchantEmail,
        BigDecimal walletBalance,
        long totalInvoices,
        long paidInvoices,
        long pendingInvoices,
        BigDecimal totalRevenue,
        BigDecimal outstandingAmount,
        BigDecimal averageInvoiceValue
) {
}