package com.hedi.payflow.invoice.service;

import com.hedi.payflow.common.service.ReferenceGeneratorService;
import com.hedi.payflow.invoice.dto.CreateInvoiceRequest;
import com.hedi.payflow.invoice.dto.InvoiceResponse;
import com.hedi.payflow.invoice.entity.Invoice;
import com.hedi.payflow.invoice.entity.InvoiceStatus;
import com.hedi.payflow.invoice.repository.InvoiceRepository;
import com.hedi.payflow.ledger.service.LedgerService;
import com.hedi.payflow.notification.entity.NotificationType;
import com.hedi.payflow.notification.service.NotificationService;
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

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final ReferenceGeneratorService referenceGeneratorService;
    private final LedgerService ledgerService;
    private final NotificationService notificationService;

    public InvoiceResponse createInvoice(Authentication authentication, CreateInvoiceRequest request) {
        User merchant = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (merchant.getRole() != Role.MERCHANT) {
            throw new RuntimeException("Only merchants can create invoices");
        }

        Invoice invoice = Invoice.builder()
                .invoiceNumber(referenceGeneratorService.generate("INV"))
                .customerEmail(request.getCustomerEmail())
                .description(request.getDescription())
                .amount(request.getAmount())
                .status(InvoiceStatus.PENDING)
                .merchant(merchant)
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);

        notificationService.create(
                merchant,
                NotificationType.INVOICE_CREATED,
                "Invoice created",
                "Invoice " + savedInvoice.getInvoiceNumber()
                        + " created for " + savedInvoice.getCustomerEmail()
        );

        return mapToResponse(savedInvoice);
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

        String paymentReference = referenceGeneratorService.generate("INVPAY");

        WalletTransaction outgoingPayment = WalletTransaction.builder()
                .reference(paymentReference + "-OUT")
                .amount(invoice.getAmount())
                .currency(customerWallet.getCurrency())
                .type(TransactionType.PAYMENT)
                .status(TransactionStatus.PENDING)
                .description("Invoice payment " + invoice.getInvoiceNumber())
                .senderWallet(customerWallet)
                .receiverWallet(merchantWallet)
                .build();

        WalletTransaction incomingPayment = WalletTransaction.builder()
                .reference(paymentReference + "-IN")
                .amount(invoice.getAmount())
                .currency(merchantWallet.getCurrency())
                .type(TransactionType.PAYMENT)
                .status(TransactionStatus.PENDING)
                .description("Invoice payment " + invoice.getInvoiceNumber())
                .senderWallet(customerWallet)
                .receiverWallet(merchantWallet)
                .build();

        WalletTransaction savedOutgoingPayment = transactionRepository.save(outgoingPayment);
        WalletTransaction savedIncomingPayment = transactionRepository.save(incomingPayment);

        try {
            customerWallet.setBalance(customerWallet.getBalance().subtract(invoice.getAmount()));
            merchantWallet.setBalance(merchantWallet.getBalance().add(invoice.getAmount()));

            walletRepository.save(customerWallet);
            walletRepository.save(merchantWallet);

            ledgerService.postWalletMovement(
                    savedOutgoingPayment.getReference(),
                    customer,
                    invoice.getMerchant(),
                    invoice.getAmount(),
                    customerWallet.getCurrency(),
                    "Invoice payment " + invoice.getInvoiceNumber(),
                    savedOutgoingPayment
            );

            savedOutgoingPayment.setStatus(TransactionStatus.SUCCESS);
            savedIncomingPayment.setStatus(TransactionStatus.SUCCESS);

            transactionRepository.save(savedOutgoingPayment);
            transactionRepository.save(savedIncomingPayment);

            invoice.setStatus(InvoiceStatus.PAID);
            invoice.setPaidAt(LocalDateTime.now());

            Invoice savedInvoice = invoiceRepository.save(invoice);

            notificationService.create(
                    customer,
                    NotificationType.INVOICE_PAID,
                    "Payment successful",
                    "You paid invoice " + savedInvoice.getInvoiceNumber()
                            + " for " + savedInvoice.getAmount() + " "
                            + customerWallet.getCurrency()
            );

            notificationService.create(
                    savedInvoice.getMerchant(),
                    NotificationType.INVOICE_PAID,
                    "Invoice paid",
                    "Invoice " + savedInvoice.getInvoiceNumber()
                            + " has been paid by " + customer.getEmail()
            );

            return mapToResponse(savedInvoice);

        } catch (Exception e) {
            savedOutgoingPayment.setStatus(TransactionStatus.FAILED);
            savedIncomingPayment.setStatus(TransactionStatus.FAILED);

            transactionRepository.save(savedOutgoingPayment);
            transactionRepository.save(savedIncomingPayment);

            throw e;
        }
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