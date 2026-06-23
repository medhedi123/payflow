package com.hedi.payflow.invoice.repository;

import com.hedi.payflow.invoice.entity.Invoice;
import com.hedi.payflow.invoice.entity.InvoiceStatus;
import com.hedi.payflow.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;


public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByMerchantOrderByCreatedAtDesc(User merchant);
    long countByMerchant(User merchant);
    long countByMerchantAndStatus(User merchant, InvoiceStatus status);
    List<Invoice> findByMerchantAndStatus(User merchant, InvoiceStatus status);
    Optional<Invoice> findByIdAndStatus(Long id, InvoiceStatus status);
}