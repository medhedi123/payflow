package com.hedi.payflow.transaction.controller;

import com.hedi.payflow.transaction.dto.ReversalRequest;
import com.hedi.payflow.transaction.dto.ReversalResponse;
import com.hedi.payflow.transaction.service.ReversalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reversals")
@RequiredArgsConstructor
public class ReversalController {

    private final ReversalService reversalService;

    @PostMapping("/{transactionId}")
    public ReversalResponse reverseTransaction(
            @PathVariable Long transactionId,
            @Valid @RequestBody ReversalRequest request
    ) {
        return reversalService.reverseTransaction(transactionId, request);
    }
}