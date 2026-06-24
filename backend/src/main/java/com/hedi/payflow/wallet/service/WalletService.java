package com.hedi.payflow.wallet.service;

import com.hedi.payflow.common.service.ReferenceGeneratorService;
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

@Service
@RequiredArgsConstructor
public class WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final LedgerService ledgerService;
    private final ReferenceGeneratorService referenceGeneratorService;

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

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        Wallet savedWallet = walletRepository.save(wallet);

        String reference = referenceGeneratorService.generate("DEP");

        WalletTransaction transaction = WalletTransaction.builder()
                .reference(reference)
                .amount(request.getAmount())
                .currency(savedWallet.getCurrency())
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCESS)
                .description("Wallet deposit")
                .receiverWallet(savedWallet)
                .build();

        WalletTransaction savedTransaction = transactionRepository.save(transaction);

        ledgerService.postDeposit(
                savedTransaction.getReference(),
                user,
                request.getAmount(),
                savedWallet.getCurrency(),
                "Wallet deposit",
                savedTransaction
        );

        return mapToResponse(savedWallet, user);
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