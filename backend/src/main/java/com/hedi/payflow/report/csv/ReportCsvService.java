package com.hedi.payflow.report.csv;

import com.hedi.payflow.transaction.entity.WalletTransaction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportCsvService {

    public byte[] generateTransactionsCsv(List<WalletTransaction> transactions) {
        StringBuilder csv = new StringBuilder();

        csv.append("Reference,Type,Status,Amount,Currency,Description,Created At\n");

        for (WalletTransaction tx : transactions) {
            csv.append(escape(tx.getReference())).append(",");
            csv.append(tx.getType()).append(",");
            csv.append(tx.getStatus()).append(",");
            csv.append(tx.getAmount()).append(",");
            csv.append(tx.getCurrency()).append(",");
            csv.append(escape(tx.getDescription())).append(",");
            csv.append(tx.getCreatedAt()).append("\n");
        }

        return csv.toString().getBytes();
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }

        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}