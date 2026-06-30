package com.hedi.payflow.report.controller;

import com.hedi.payflow.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/transactions.csv")
    public ResponseEntity<byte[]> exportTransactionsCsv(Authentication authentication) {
        byte[] csv = reportService.exportMyTransactionsCsv(authentication);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payflow-transactions.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}