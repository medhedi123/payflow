package com.hedi.payflow.ledger.repository;

import com.hedi.payflow.ledger.entity.LedgerAccount;
import com.hedi.payflow.ledger.entity.LedgerAccountType;
import com.hedi.payflow.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LedgerAccountRepository
        extends JpaRepository<LedgerAccount, Long> {

    Optional<LedgerAccount> findByUser(User user);

    Optional<LedgerAccount> findByAccountNumber(String accountNumber);

    Optional<LedgerAccount> findByTypeAndCurrency(LedgerAccountType type, String currency);
}