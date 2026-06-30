package com.hedi.payflow.transaction.repository;

import com.hedi.payflow.transaction.entity.WalletTransaction;
import com.hedi.payflow.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hedi.payflow.transaction.entity.TransactionStatus;
import java.util.Optional;

import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction> findBySenderWalletOrReceiverWalletOrderByCreatedAtDesc(
            Wallet senderWallet,
            Wallet receiverWallet
    );
    Optional<WalletTransaction> findByIdAndStatus(Long id, TransactionStatus status);
}