package com.hedi.payflow.ledger.dto;

import com.hedi.payflow.ledger.entity.LedgerEntryType;

import java.math.BigDecimal;

public record LedgerEntryResponse(
        Long id,
        String accountNumber,
        LedgerEntryType type,
        BigDecimal amount,
        String currency,
        String description
) {
}