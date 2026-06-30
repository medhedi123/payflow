package com.hedi.payflow.merchant.report.controller;

import com.hedi.payflow.merchant.report.dto.MerchantReportResponse;
import com.hedi.payflow.merchant.report.service.MerchantReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/merchant/reports")
@RequiredArgsConstructor
public class MerchantReportController {

    private final MerchantReportService merchantReportService;

    @GetMapping("/summary")
    public MerchantReportResponse getSummary(Authentication authentication) {
        return merchantReportService.getSummary(authentication);
    }

    @GetMapping("/summary.csv")
    public ResponseEntity<byte[]> exportSummaryCsv(Authentication authentication) {
        byte[] csv = merchantReportService.exportSummaryCsv(authentication);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=merchant-report-summary.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}