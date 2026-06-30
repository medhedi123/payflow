package com.hedi.payflow.ledger.service;

import com.hedi.payflow.ledger.entity.*;
import com.hedi.payflow.ledger.repository.LedgerAccountRepository;
import com.hedi.payflow.ledger.repository.LedgerEntryRepository;
import com.hedi.payflow.ledger.repository.LedgerTransactionRepository;
import com.hedi.payflow.transaction.entity.WalletTransaction;
import com.hedi.payflow.user.entity.Role;
import com.hedi.payflow.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final LedgerAccountRepository ledgerAccountRepository;
    private final LedgerTransactionRepository ledgerTransactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public LedgerAccount createAccountForUser(User user, String currency) {
        LedgerAccountType type = user.getRole() == Role.MERCHANT
                ? LedgerAccountType.MERCHANT_WALLET
                : LedgerAccountType.CUSTOMER_WALLET;

        LedgerAccount account = LedgerAccount.builder()
                .accountNumber("LA-" + UUID.randomUUID())
                .type(type)
                .user(user)
                .currency(currency)
                .balance(BigDecimal.ZERO)
                .active(true)
                .build();

        return ledgerAccountRepository.save(account);
    }

    public LedgerAccount getOrCreateAccount(User user, String currency) {
        return ledgerAccountRepository.findByUser(user)
                .orElseGet(() -> createAccountForUser(user, currency));
    }

    private LedgerAccount getOrCreatePlatformCashAccount(String currency) {
        return ledgerAccountRepository
                .findByTypeAndCurrency(LedgerAccountType.PLATFORM_CASH, currency)
                .orElseGet(() -> {
                    LedgerAccount account = LedgerAccount.builder()
                            .accountNumber("LA-PLATFORM-CASH-" + currency)
                            .type(LedgerAccountType.PLATFORM_CASH)
                            .currency(currency)
                            .balance(BigDecimal.ZERO)
                            .active(true)
                            .build();

                    return ledgerAccountRepository.save(account);
                });
    }

    public LedgerTransaction postWalletMovement(
            String reference,
            User debitUser,
            User creditUser,
            BigDecimal amount,
            String currency,
            String description,
            WalletTransaction walletTransaction
    ) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Ledger amount must be greater than zero");
        }

        LedgerAccount debitAccount = getOrCreateAccount(debitUser, currency);
        LedgerAccount creditAccount = getOrCreateAccount(creditUser, currency);

        LedgerTransaction savedTransaction = ledgerTransactionRepository.save(
                LedgerTransaction.builder()
                        .reference(reference != null ? reference : "LED-" + UUID.randomUUID())
                        .status(LedgerTransactionStatus.PENDING)
                        .walletTransaction(walletTransaction)
                        .description(description)
                        .build()
        );

        ledgerEntryRepository.save(LedgerEntry.builder()
                .ledgerTransaction(savedTransaction)
                .ledgerAccount(debitAccount)
                .type(LedgerEntryType.DEBIT)
                .amount(amount)
                .currency(currency)
                .description(description)
                .build());

        ledgerEntryRepository.save(LedgerEntry.builder()
                .ledgerTransaction(savedTransaction)
                .ledgerAccount(creditAccount)
                .type(LedgerEntryType.CREDIT)
                .amount(amount)
                .currency(currency)
                .description(description)
                .build());

        debitAccount.setBalance(debitAccount.getBalance().subtract(amount));
        creditAccount.setBalance(creditAccount.getBalance().add(amount));

        ledgerAccountRepository.save(debitAccount);
        ledgerAccountRepository.save(creditAccount);

        savedTransaction.setStatus(LedgerTransactionStatus.POSTED);
        savedTransaction.setPostedAt(LocalDateTime.now());

        return ledgerTransactionRepository.save(savedTransaction);
    }

    public LedgerTransaction postDeposit(
            String reference,
            User user,
            BigDecimal amount,
            String currency,
            String description,
            WalletTransaction walletTransaction
    ) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Ledger amount must be greater than zero");
        }

        LedgerAccount platformCash = getOrCreatePlatformCashAccount(currency);
        LedgerAccount userAccount = getOrCreateAccount(user, currency);

        LedgerTransaction savedTransaction = ledgerTransactionRepository.save(
                LedgerTransaction.builder()
                        .reference(reference != null ? reference : "LED-" + UUID.randomUUID())
                        .status(LedgerTransactionStatus.PENDING)
                        .walletTransaction(walletTransaction)
                        .description(description)
                        .build()
        );

        ledgerEntryRepository.save(LedgerEntry.builder()
                .ledgerTransaction(savedTransaction)
                .ledgerAccount(platformCash)
                .type(LedgerEntryType.DEBIT)
                .amount(amount)
                .currency(currency)
                .description(description)
                .build());

        ledgerEntryRepository.save(LedgerEntry.builder()
                .ledgerTransaction(savedTransaction)
                .ledgerAccount(userAccount)
                .type(LedgerEntryType.CREDIT)
                .amount(amount)
                .currency(currency)
                .description(description)
                .build());

        platformCash.setBalance(platformCash.getBalance().add(amount));
        userAccount.setBalance(userAccount.getBalance().add(amount));

        ledgerAccountRepository.save(platformCash);
        ledgerAccountRepository.save(userAccount);

        savedTransaction.setStatus(LedgerTransactionStatus.POSTED);
        savedTransaction.setPostedAt(LocalDateTime.now());

        return ledgerTransactionRepository.save(savedTransaction);
    }

    public LedgerTransaction postReversal(
            String reference,
            User originalDebitUser,
            User originalCreditUser,
            BigDecimal amount,
            String currency,
            String description,
            WalletTransaction reversalTransaction
    ) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Ledger amount must be greater than zero");
        }

        LedgerAccount originalDebitAccount = getOrCreateAccount(originalDebitUser, currency);
        LedgerAccount originalCreditAccount = getOrCreateAccount(originalCreditUser, currency);

        LedgerTransaction savedTransaction = ledgerTransactionRepository.save(
                LedgerTransaction.builder()
                        .reference(reference != null ? reference : "REV-" + UUID.randomUUID())
                        .status(LedgerTransactionStatus.PENDING)
                        .walletTransaction(reversalTransaction)
                        .description(description)
                        .build()
        );

        ledgerEntryRepository.save(LedgerEntry.builder()
                .ledgerTransaction(savedTransaction)
                .ledgerAccount(originalCreditAccount)
                .type(LedgerEntryType.DEBIT)
                .amount(amount)
                .currency(currency)
                .description(description)
                .build());

        ledgerEntryRepository.save(LedgerEntry.builder()
                .ledgerTransaction(savedTransaction)
                .ledgerAccount(originalDebitAccount)
                .type(LedgerEntryType.CREDIT)
                .amount(amount)
                .currency(currency)
                .description(description)
                .build());

        originalCreditAccount.setBalance(originalCreditAccount.getBalance().subtract(amount));
        originalDebitAccount.setBalance(originalDebitAccount.getBalance().add(amount));

        ledgerAccountRepository.save(originalCreditAccount);
        ledgerAccountRepository.save(originalDebitAccount);

        savedTransaction.setStatus(LedgerTransactionStatus.POSTED);
        savedTransaction.setPostedAt(LocalDateTime.now());

        return ledgerTransactionRepository.save(savedTransaction);
    }
}