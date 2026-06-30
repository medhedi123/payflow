package com.hedi.payflow.merchant.report.csv;

import com.hedi.payflow.merchant.report.dto.MerchantReportResponse;
import org.springframework.stereotype.Service;

@Service
public class MerchantReportCsvService {

    public byte[] generateSummaryCsv(MerchantReportResponse report) {
        StringBuilder csv = new StringBuilder();

        csv.append("Metric,Value\n");
        csv.append("Merchant Email,").append(report.merchantEmail()).append("\n");
        csv.append("Wallet Balance,").append(report.walletBalance()).append("\n");
        csv.append("Total Invoices,").append(report.totalInvoices()).append("\n");
        csv.append("Paid Invoices,").append(report.paidInvoices()).append("\n");
        csv.append("Pending Invoices,").append(report.pendingInvoices()).append("\n");
        csv.append("Total Revenue,").append(report.totalRevenue()).append("\n");
        csv.append("Outstanding Amount,").append(report.outstandingAmount()).append("\n");
        csv.append("Average Invoice Value,").append(report.averageInvoiceValue()).append("\n");

        return csv.toString().getBytes();
    }
}