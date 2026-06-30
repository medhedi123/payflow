package com.hedi.payflow.merchant.report.service;

import com.hedi.payflow.invoice.entity.Invoice;
import com.hedi.payflow.invoice.entity.InvoiceStatus;
import com.hedi.payflow.invoice.repository.InvoiceRepository;
import com.hedi.payflow.merchant.report.csv.MerchantReportCsvService;
import com.hedi.payflow.merchant.report.dto.MerchantReportResponse;
import com.hedi.payflow.user.entity.Role;
import com.hedi.payflow.user.entity.User;
import com.hedi.payflow.user.repository.UserRepository;
import com.hedi.payflow.wallet.entity.Wallet;
import com.hedi.payflow.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantReportService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final InvoiceRepository invoiceRepository;
    private final MerchantReportCsvService merchantReportCsvService;

    public MerchantReportResponse getSummary(Authentication authentication) {
        User merchant = getAuthenticatedMerchant(authentication);

        Wallet wallet = walletRepository.findByUser(merchant)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        long totalInvoices = invoiceRepository.countByMerchant(merchant);
        long paidInvoices = invoiceRepository.countByMerchantAndStatus(merchant, InvoiceStatus.PAID);
        long pendingInvoices = invoiceRepository.countByMerchantAndStatus(merchant, InvoiceStatus.PENDING);

        BigDecimal totalRevenue = invoiceRepository
                .findByMerchantAndStatus(merchant, InvoiceStatus.PAID)
                .stream()
                .map(Invoice::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal outstandingAmount = invoiceRepository
                .findByMerchantAndStatus(merchant, InvoiceStatus.PENDING)
                .stream()
                .map(Invoice::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Invoice> allInvoices = invoiceRepository.findByMerchantOrderByCreatedAtDesc(merchant);

        BigDecimal averageInvoiceValue = allInvoices.isEmpty()
                ? BigDecimal.ZERO
                : allInvoices.stream()
                        .map(Invoice::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(allInvoices.size()), 2, RoundingMode.HALF_UP);

        return new MerchantReportResponse(
                merchant.getEmail(),
                wallet.getBalance(),
                totalInvoices,
                paidInvoices,
                pendingInvoices,
                totalRevenue,
                outstandingAmount,
                averageInvoiceValue
        );
    }

    public byte[] exportSummaryCsv(Authentication authentication) {
        MerchantReportResponse report = getSummary(authentication);
        return merchantReportCsvService.generateSummaryCsv(report);
    }

    private User getAuthenticatedMerchant(Authentication authentication) {
        User merchant = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (merchant.getRole() != Role.MERCHANT) {
            throw new RuntimeException("Only merchants can access merchant reports");
        }

        return merchant;
    }
}