package com.hedi.payflow.statement.controller;

import com.hedi.payflow.statement.service.StatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/statements")
@RequiredArgsConstructor
public class StatementController {

    private final StatementService statementService;

    @GetMapping("/wallet.pdf")
    public ResponseEntity<byte[]> downloadWalletStatement(Authentication authentication) {
        byte[] pdf = statementService.generateMyWalletStatement(authentication);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payflow-wallet-statement.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}