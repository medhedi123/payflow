package com.hedi.payflow.dashboard.service;

import com.hedi.payflow.dashboard.dto.DashboardSummaryResponse;
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
import com.hedi.payflow.dashboard.dto.MerchantDashboardResponse;
import com.hedi.payflow.invoice.entity.Invoice;
import com.hedi.payflow.invoice.entity.InvoiceStatus;
import com.hedi.payflow.invoice.repository.InvoiceRepository;
import com.hedi.payflow.user.entity.Role;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final InvoiceRepository invoiceRepository;

    public DashboardSummaryResponse getSummary(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        List<WalletTransaction> transactions =
                transactionRepository.findBySenderWalletOrReceiverWalletOrderByCreatedAtDesc(wallet, wallet);

        BigDecimal totalDeposits = sumByType(transactions, TransactionType.DEPOSIT);
        BigDecimal totalTransfersSent = sumByType(transactions, TransactionType.TRANSFER_OUT);
        BigDecimal totalTransfersReceived = sumByType(transactions, TransactionType.TRANSFER_IN);

        return new DashboardSummaryResponse(
                wallet.getBalance(),
                totalDeposits,
                totalTransfersSent,
                totalTransfersReceived,
                transactions.size()
        );
    }

    private BigDecimal sumByType(List<WalletTransaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(tx -> tx.getType() == type)
                .map(WalletTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public MerchantDashboardResponse getMerchantDashboard(Authentication authentication) {
        User merchant = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (merchant.getRole() != Role.MERCHANT) {
                throw new RuntimeException("Only merchants can access this dashboard");
        }

        Wallet wallet = walletRepository.findByUser(merchant)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        long totalInvoices = invoiceRepository.countByMerchant(merchant);
        long pendingInvoices = invoiceRepository.countByMerchantAndStatus(merchant, InvoiceStatus.PENDING);
        long paidInvoices = invoiceRepository.countByMerchantAndStatus(merchant, InvoiceStatus.PAID);

        BigDecimal totalRevenue = invoiceRepository
                .findByMerchantAndStatus(merchant, InvoiceStatus.PAID)
                .stream()
                .map(Invoice::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new MerchantDashboardResponse(
                totalInvoices,
                pendingInvoices,
                paidInvoices,
                totalRevenue,
                wallet.getBalance()
        );
        }
}