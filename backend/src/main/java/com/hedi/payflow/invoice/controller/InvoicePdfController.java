package com.hedi.payflow.invoice.controller;

import com.hedi.payflow.invoice.entity.Invoice;
import com.hedi.payflow.invoice.pdf.InvoicePdfService;
import com.hedi.payflow.invoice.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoicePdfController {

    private final InvoicePdfService invoicePdfService;
    private final InvoiceRepository invoiceRepository;

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        byte[] pdf = invoicePdfService.generateInvoicePdf(invoice);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=invoice-" + invoice.getInvoiceNumber() + ".pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}