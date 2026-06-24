package com.hedi.payflow.ledger.repository;

import com.hedi.payflow.ledger.entity.LedgerEntry;
import com.hedi.payflow.ledger.entity.LedgerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LedgerEntryRepository
        extends JpaRepository<LedgerEntry, Long> {

    List<LedgerEntry> findByLedgerTransaction(LedgerTransaction transaction);
}