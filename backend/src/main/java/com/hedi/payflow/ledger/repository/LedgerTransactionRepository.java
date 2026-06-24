package com.hedi.payflow.ledger.repository;

import com.hedi.payflow.ledger.entity.LedgerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LedgerTransactionRepository
        extends JpaRepository<LedgerTransaction, Long> {

    Optional<LedgerTransaction> findByReference(String reference);
}