package com.hedi.payflow.transaction.service;

import com.hedi.payflow.transaction.entity.TransactionStatus;
import com.hedi.payflow.transaction.entity.WalletTransaction;
import com.hedi.payflow.transaction.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionLifecycleService {

    private final WalletTransactionRepository repository;

    public WalletTransaction markPending(WalletTransaction tx) {
        tx.setStatus(TransactionStatus.PENDING);
        return repository.save(tx);
    }

    public WalletTransaction markSuccess(WalletTransaction tx) {
        tx.setStatus(TransactionStatus.SUCCESS);
        return repository.save(tx);
    }

    public WalletTransaction markFailed(WalletTransaction tx) {
        tx.setStatus(TransactionStatus.FAILED);
        return repository.save(tx);
    }

    public WalletTransaction markReversed(WalletTransaction tx) {
        tx.setStatus(TransactionStatus.REVERSED);
        return repository.save(tx);
    }
}