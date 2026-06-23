package com.hedi.payflow.invoice.dto;

import com.hedi.payflow.invoice.entity.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InvoiceResponse {

    private Long id;
    private String invoiceNumber;
    private String customerEmail;
    private String description;
    private BigDecimal amount;
    private InvoiceStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}