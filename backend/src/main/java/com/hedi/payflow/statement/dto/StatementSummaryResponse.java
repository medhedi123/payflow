package com.hedi.payflow.statement.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StatementSummaryResponse(
        String customerEmail,
        String walletCurrency,
        BigDecimal openingBalance,
        BigDecimal closingBalance,
        BigDecimal totalCredits,
        BigDecimal totalDebits,
        long totalTransactions,
        LocalDateTime generatedAt
) {
}