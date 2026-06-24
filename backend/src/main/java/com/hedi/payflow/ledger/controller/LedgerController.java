package com.hedi.payflow.ledger.controller;

import com.hedi.payflow.ledger.dto.LedgerEntryResponse;
import com.hedi.payflow.ledger.entity.LedgerTransaction;
import com.hedi.payflow.ledger.repository.LedgerEntryRepository;
import com.hedi.payflow.ledger.repository.LedgerTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerTransactionRepository ledgerTransactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    @GetMapping("/{reference}")
    public List<LedgerEntryResponse> getLedger(@PathVariable String reference) {

        LedgerTransaction transaction =
                ledgerTransactionRepository.findByReference(reference)
                        .orElseThrow(() -> new RuntimeException("Ledger transaction not found"));

        return ledgerEntryRepository.findByLedgerTransaction(transaction)
                .stream()
                .map(entry -> new LedgerEntryResponse(
                        entry.getId(),
                        entry.getLedgerAccount().getAccountNumber(),
                        entry.getType(),
                        entry.getAmount(),
                        entry.getCurrency(),
                        entry.getDescription()
                ))
                .toList();
    }
}