package com.hedi.payflow.wallet.service;

import com.hedi.payflow.ledger.service.LedgerService;
import com.hedi.payflow.transaction.entity.TransactionStatus;
import com.hedi.payflow.transaction.entity.TransactionType;
import com.hedi.payflow.transaction.entity.WalletTransaction;
import com.hedi.payflow.transaction.repository.WalletTransactionRepository;
import com.hedi.payflow.user.entity.User;
import com.hedi.payflow.user.repository.UserRepository;
import com.hedi.payflow.wallet.dto.DepositRequest;
import com.hedi.payflow.wallet.dto.WalletResponse;
import com.hedi.payflow.wallet.entity.Wallet;
import com.hedi.payflow.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.hedi.payflow.notification.entity.NotificationType;
import com.hedi.payflow.notification.service.NotificationService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final LedgerService ledgerService;
    private final NotificationService notificationService;

    public WalletResponse getMyWallet(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        return mapToResponse(wallet, user);
    }

    public WalletResponse deposit(Authentication authentication, DepositRequest request) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        WalletTransaction transaction = WalletTransaction.builder()
                .reference("DEP-" + UUID.randomUUID())
                .amount(request.getAmount())
                .currency(wallet.getCurrency())
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.PENDING)
                .description("Wallet deposit")
                .receiverWallet(wallet)
                .build();

        WalletTransaction savedTransaction = transactionRepository.save(transaction);

        try {
            wallet.setBalance(wallet.getBalance().add(request.getAmount()));
            Wallet savedWallet = walletRepository.save(wallet);

            ledgerService.postDeposit(
                    savedTransaction.getReference(),
                    user,
                    request.getAmount(),
                    savedWallet.getCurrency(),
                    "Wallet deposit",
                    savedTransaction
            );

            notificationService.create(
                     user,
                     NotificationType.DEPOSIT_RECEIVED,
                     "Deposit received",
                     request.getAmount() + " " + savedWallet.getCurrency() +
                     " has been added to your wallet."
            );

            savedTransaction.setStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(savedTransaction);

            return mapToResponse(savedWallet, user);

        } catch (Exception e) {
            savedTransaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(savedTransaction);

            throw e;
        }
    }

    private WalletResponse mapToResponse(Wallet wallet, User user) {
        return new WalletResponse(
                wallet.getId(),
                user.getId(),
                wallet.getBalance(),
                wallet.getCurrency(),
                wallet.getStatus(),
                wallet.getCreatedAt()
        );
    }
}