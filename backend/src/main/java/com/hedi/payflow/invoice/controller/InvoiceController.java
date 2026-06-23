package com.hedi.payflow.invoice.controller;

import com.hedi.payflow.invoice.dto.CreateInvoiceRequest;
import com.hedi.payflow.invoice.dto.InvoiceResponse;
import com.hedi.payflow.invoice.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public InvoiceResponse createInvoice(
            Authentication authentication,
            @Valid @RequestBody CreateInvoiceRequest request
    ) {
        return invoiceService.createInvoice(authentication, request);
    }
    @GetMapping("/me")
    public List<InvoiceResponse> getMyInvoices(Authentication authentication) {
        return invoiceService.getMyInvoices(authentication);
    }
    @PostMapping("/{id}/pay")
    public InvoiceResponse payInvoice(
            Authentication authentication,
            @PathVariable Long id
    ) {
        return invoiceService.payInvoice(authentication, id);
    }
}