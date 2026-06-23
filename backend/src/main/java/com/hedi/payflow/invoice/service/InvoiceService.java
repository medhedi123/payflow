package com.hedi.payflow.invoice.service;

import com.hedi.payflow.invoice.dto.CreateInvoiceRequest;
import com.hedi.payflow.invoice.dto.InvoiceResponse;
import com.hedi.payflow.invoice.entity.Invoice;
import com.hedi.payflow.invoice.entity.InvoiceStatus;
import com.hedi.payflow.invoice.repository.InvoiceRepository;
import com.hedi.payflow.transaction.entity.TransactionStatus;
import com.hedi.payflow.transaction.entity.TransactionType;
import com.hedi.payflow.transaction.entity.WalletTransaction;
import com.hedi.payflow.transaction.repository.WalletTransactionRepository;
import com.hedi.payflow.user.entity.Role;
import com.hedi.payflow.user.entity.User;
import com.hedi.payflow.user.repository.UserRepository;
import com.hedi.payflow.wallet.entity.Wallet;
import com.hedi.payflow.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    public InvoiceResponse createInvoice(Authentication authentication, CreateInvoiceRequest request) {
        User merchant = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (merchant.getRole() != Role.MERCHANT) {
            throw new RuntimeException("Only merchants can create invoices");
        }

        Invoice invoice = Invoice.builder()
                .invoiceNumber("INV-" + UUID.randomUUID())
                .customerEmail(request.getCustomerEmail())
                .description(request.getDescription())
                .amount(request.getAmount())
                .status(InvoiceStatus.PENDING)
                .merchant(merchant)
                .build();

        return mapToResponse(invoiceRepository.save(invoice));
    }

    public List<InvoiceResponse> getMyInvoices(Authentication authentication) {
        User merchant = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return invoiceRepository.findByMerchantOrderByCreatedAtDesc(merchant)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public InvoiceResponse payInvoice(Authentication authentication, Long invoiceId) {
        User customer = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Invoice invoice = invoiceRepository.findByIdAndStatus(invoiceId, InvoiceStatus.PENDING)
                .orElseThrow(() -> new RuntimeException("Invoice not found or already paid"));

        Wallet customerWallet = walletRepository.findByUser(customer)
                .orElseThrow(() -> new RuntimeException("Customer wallet not found"));

        Wallet merchantWallet = walletRepository.findByUser(invoice.getMerchant())
                .orElseThrow(() -> new RuntimeException("Merchant wallet not found"));

        if (customerWallet.getBalance().compareTo(invoice.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        customerWallet.setBalance(customerWallet.getBalance().subtract(invoice.getAmount()));
        merchantWallet.setBalance(merchantWallet.getBalance().add(invoice.getAmount()));

        walletRepository.save(customerWallet);
        walletRepository.save(merchantWallet);

        String reference = "INVPAY-" + UUID.randomUUID();

        transactionRepository.save(WalletTransaction.builder()
                .reference(reference + "-OUT")
                .amount(invoice.getAmount())
                .currency(customerWallet.getCurrency())
                .type(TransactionType.PAYMENT)
                .status(TransactionStatus.SUCCESS)
                .description("Invoice payment " + invoice.getInvoiceNumber())
                .senderWallet(customerWallet)
                .receiverWallet(merchantWallet)
                .build());

        transactionRepository.save(WalletTransaction.builder()
                .reference(reference + "-IN")
                .amount(invoice.getAmount())
                .currency(merchantWallet.getCurrency())
                .type(TransactionType.PAYMENT)
                .status(TransactionStatus.SUCCESS)
                .description("Invoice payment " + invoice.getInvoiceNumber())
                .senderWallet(customerWallet)
                .receiverWallet(merchantWallet)
                .build());

        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidAt(LocalDateTime.now());

        return mapToResponse(invoiceRepository.save(invoice));
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getCustomerEmail(),
                invoice.getDescription(),
                invoice.getAmount(),
                invoice.getStatus(),
                invoice.getCreatedAt(),
                invoice.getPaidAt()
        );
    }
}