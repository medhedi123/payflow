package com.hedi.payflow.transaction.controller;

import com.hedi.payflow.transaction.dto.TransactionResponse;
import com.hedi.payflow.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.hedi.payflow.transaction.dto.TransferRequest;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/me")
    public List<TransactionResponse> getMyTransactions(Authentication authentication) {
        return transactionService.getMyTransactions(authentication);
    }
    @PostMapping("/transfer")
    public TransactionResponse transfer(
            Authentication authentication,
            @Valid @RequestBody TransferRequest request
    ) {
        return transactionService.transfer(authentication, request);
    }
}