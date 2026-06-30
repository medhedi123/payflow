package com.hedi.payflow.transaction.service;

import com.hedi.payflow.common.service.ReferenceGeneratorService;
import com.hedi.payflow.ledger.service.LedgerService;
import com.hedi.payflow.notification.entity.NotificationType;
import com.hedi.payflow.notification.service.NotificationService;
import com.hedi.payflow.transaction.dto.ReversalRequest;
import com.hedi.payflow.transaction.dto.ReversalResponse;
import com.hedi.payflow.transaction.entity.TransactionStatus;
import com.hedi.payflow.transaction.entity.TransactionType;
import com.hedi.payflow.transaction.entity.WalletTransaction;
import com.hedi.payflow.transaction.repository.WalletTransactionRepository;
import com.hedi.payflow.user.entity.User;
import com.hedi.payflow.wallet.entity.Wallet;
import com.hedi.payflow.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReversalService {

    private final WalletTransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final LedgerService ledgerService;
    private final ReferenceGeneratorService referenceGeneratorService;
    private final NotificationService notificationService;

    public ReversalResponse reverseTransaction(Long transactionId, ReversalRequest request) {

        WalletTransaction original = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        validateReversalEligibility(original);

        Wallet senderWallet = original.getSenderWallet();
        Wallet receiverWallet = original.getReceiverWallet();

        User senderUser = senderWallet.getUser();
        User receiverUser = receiverWallet.getUser();

        receiverWallet.setBalance(receiverWallet.getBalance().subtract(original.getAmount()));
        senderWallet.setBalance(senderWallet.getBalance().add(original.getAmount()));

        walletRepository.save(receiverWallet);
        walletRepository.save(senderWallet);

        String reversalReference = referenceGeneratorService.generate("REV");

        WalletTransaction reversal = WalletTransaction.builder()
                .reference(reversalReference)
                .amount(original.getAmount())
                .currency(original.getCurrency())
                .type(TransactionType.REVERSAL)
                .status(TransactionStatus.SUCCESS)
                .description("Reversal of " + original.getReference())
                .senderWallet(receiverWallet)
                .receiverWallet(senderWallet)
                .reversedTransaction(original)
                .reversalReason(request.reason())
                .build();

        WalletTransaction savedReversal = transactionRepository.save(reversal);

        ledgerService.postReversal(
                savedReversal.getReference(),
                senderUser,
                receiverUser,
                original.getAmount(),
                original.getCurrency(),
                "Reversal of " + original.getReference(),
                savedReversal
        );

        original.setStatus(TransactionStatus.REVERSED);
        original.setReversalReason(request.reason());
        original.setReversedAt(LocalDateTime.now());

        transactionRepository.save(original);

        notificationService.create(
                senderUser,
                NotificationType.TRANSACTION_REVERSED,
                "Transaction reversed",
                "Your transfer " + original.getReference()
                        + " was reversed. Reason: " + request.reason()
        );

        notificationService.create(
                receiverUser,
                NotificationType.TRANSACTION_REVERSED,
                "Transaction reversed",
                "Transfer " + original.getReference()
                        + " was reversed. Reason: " + request.reason()
        );

        return new ReversalResponse(
                original.getId(),
                original.getReference(),
                savedReversal.getReference(),
                original.getStatus().name(),
                request.reason()
        );
    }

    private void validateReversalEligibility(WalletTransaction original) {

        if (original.getStatus() != TransactionStatus.SUCCESS) {
            throw new RuntimeException("Only SUCCESS transactions can be reversed");
        }

        if (original.getType() != TransactionType.TRANSFER_OUT) {
            throw new RuntimeException("Only outgoing transfers can be reversed");
        }

        if (original.getReversedAt() != null) {
            throw new RuntimeException("Transaction already reversed");
        }

        if (original.getSenderWallet() == null || original.getReceiverWallet() == null) {
            throw new RuntimeException("This transaction type cannot be reversed");
        }
    }
}