package com.hedi.payflow.transaction.service;

import com.hedi.payflow.common.service.ReferenceGeneratorService;
import com.hedi.payflow.ledger.service.LedgerService;
import com.hedi.payflow.transaction.dto.TransactionResponse;
import com.hedi.payflow.transaction.dto.TransferRequest;
import com.hedi.payflow.transaction.entity.TransactionStatus;
import com.hedi.payflow.transaction.entity.TransactionType;
import com.hedi.payflow.transaction.entity.WalletTransaction;
import com.hedi.payflow.transaction.repository.WalletTransactionRepository;
import com.hedi.payflow.user.entity.User;
import com.hedi.payflow.user.repository.UserRepository;
import com.hedi.payflow.wallet.entity.Wallet;
import com.hedi.payflow.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final LedgerService ledgerService;
    private final ReferenceGeneratorService referenceGeneratorService;

    public List<TransactionResponse> getMyTransactions(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        return transactionRepository
                .findBySenderWalletOrReceiverWalletOrderByCreatedAtDesc(wallet, wallet)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TransactionResponse transfer(Authentication authentication, TransferRequest request) {

        User senderUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiverUser = userRepository.findByEmail(request.getReceiverEmail())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (senderUser.getEmail().equals(receiverUser.getEmail())) {
                throw new RuntimeException("You cannot transfer money to yourself");
        }

        Wallet senderWallet = walletRepository.findByUser(senderUser)
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        Wallet receiverWallet = walletRepository.findByUser(receiverUser)
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
                throw new RuntimeException("Insufficient balance");
        }

        String transferReference = referenceGeneratorService.generate("TRF");

        WalletTransaction senderTransaction = WalletTransaction.builder()
                .reference(transferReference + "-OUT")
                .amount(request.getAmount())
                .currency(senderWallet.getCurrency())
                .type(TransactionType.TRANSFER_OUT)
                .status(TransactionStatus.PENDING)
                .description("Transfer to " + receiverUser.getEmail())
                .senderWallet(senderWallet)
                .receiverWallet(receiverWallet)
                .build();

        WalletTransaction receiverTransaction = WalletTransaction.builder()
                .reference(transferReference + "-IN")
                .amount(request.getAmount())
                .currency(receiverWallet.getCurrency())
                .type(TransactionType.TRANSFER_IN)
                .status(TransactionStatus.PENDING)
                .description("Transfer from " + senderUser.getEmail())
                .senderWallet(senderWallet)
                .receiverWallet(receiverWallet)
                .build();

        WalletTransaction savedSenderTransaction =
                transactionRepository.save(senderTransaction);

        WalletTransaction savedReceiverTransaction =
                transactionRepository.save(receiverTransaction);

        try {

                senderWallet.setBalance(
                        senderWallet.getBalance().subtract(request.getAmount())
                );

                receiverWallet.setBalance(
                        receiverWallet.getBalance().add(request.getAmount())
                );

                walletRepository.save(senderWallet);
                walletRepository.save(receiverWallet);

                ledgerService.postWalletMovement(
                        savedSenderTransaction.getReference(),
                        senderUser,
                        receiverUser,
                        request.getAmount(),
                        senderWallet.getCurrency(),
                        "Wallet transfer to " + receiverUser.getEmail(),
                        savedSenderTransaction
                );

                savedSenderTransaction.setStatus(TransactionStatus.SUCCESS);
                savedReceiverTransaction.setStatus(TransactionStatus.SUCCESS);

        } catch (Exception e) {

                savedSenderTransaction.setStatus(TransactionStatus.FAILED);
                savedReceiverTransaction.setStatus(TransactionStatus.FAILED);

                transactionRepository.save(savedSenderTransaction);
                transactionRepository.save(savedReceiverTransaction);

                throw e;
        }

        transactionRepository.save(savedSenderTransaction);
        transactionRepository.save(savedReceiverTransaction);

        return mapToResponse(savedSenderTransaction);
        }

    private TransactionResponse mapToResponse(WalletTransaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getReference(),
                tx.getAmount(),
                tx.getCurrency(),
                tx.getType(),
                tx.getStatus(),
                tx.getDescription(),
                tx.getCreatedAt()
        );
    }
}