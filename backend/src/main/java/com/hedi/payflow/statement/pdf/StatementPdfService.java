package com.hedi.payflow.statement.pdf;

import com.hedi.payflow.transaction.entity.TransactionStatus;
import com.hedi.payflow.transaction.entity.TransactionType;
import com.hedi.payflow.transaction.entity.WalletTransaction;
import com.hedi.payflow.wallet.entity.Wallet;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatementPdfService {

    private static final Color PAYFLOW_BLUE = new Color(37, 99, 235);
    private static final Color DARK = new Color(15, 23, 42);
    private static final Color LIGHT_BG = new Color(248, 250, 252);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color GREEN_BG = new Color(220, 252, 231);
    private static final Color ORANGE_BG = new Color(254, 243, 199);
    private static final Color RED_BG = new Color(254, 226, 226);
    private static final Color BLUE_BG = new Color(219, 234, 254);

    public byte[] generateWalletStatement(Wallet wallet, List<WalletTransaction> transactions) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate(), 34, 34, 34, 34);

            PdfWriter.getInstance(document, out);
            document.open();

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
            DecimalFormat moneyFormatter = new DecimalFormat("#,##0.00");

            Font brandFont = new Font(Font.HELVETICA, 22, Font.BOLD, Color.WHITE);
            Font whiteSmall = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.WHITE);
            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD, DARK);
            Font sectionFont = new Font(Font.HELVETICA, 11, Font.BOLD, DARK);
            Font normalFont = new Font(Font.HELVETICA, 8, Font.NORMAL, DARK);
            Font mutedFont = new Font(Font.HELVETICA, 8, Font.NORMAL, new Color(100, 116, 139));
            Font boldFont = new Font(Font.HELVETICA, 8, Font.BOLD, DARK);

            BigDecimal totalDebits = totalDebits(transactions);
            BigDecimal totalCredits = totalCredits(transactions);
            BigDecimal openingBalance = wallet.getBalance().subtract(totalCredits).add(totalDebits);

            addHeader(document, brandFont, whiteSmall);
            addStatementInfo(document, wallet, transactions, titleFont, sectionFont, normalFont, mutedFont);
            addSummaryCards(document, wallet, transactions, openingBalance, totalCredits, totalDebits, sectionFont, normalFont, moneyFormatter);
            addTransactionsTable(document, wallet, transactions, openingBalance, normalFont, boldFont, moneyFormatter, dateFormatter);
            addFooter(document, mutedFont);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate wallet statement PDF", e);
        }
    }

    private void addHeader(Document document, Font brandFont, Font whiteSmall) throws DocumentException {
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{70, 30});
        header.setSpacingAfter(18);

        PdfPCell left = new PdfPCell();
        left.setBackgroundColor(PAYFLOW_BLUE);
        left.setPadding(16);
        left.setBorder(Rectangle.NO_BORDER);
        left.addElement(new Paragraph("PAYFLOW OS", brandFont));
        left.addElement(new Paragraph("African Business Operating System", whiteSmall));

        PdfPCell right = new PdfPCell();
        right.setBackgroundColor(PAYFLOW_BLUE);
        right.setPadding(16);
        right.setBorder(Rectangle.NO_BORDER);
        right.addElement(new Paragraph("Official Wallet Statement", whiteSmall));
        right.addElement(new Paragraph("Business financial records", whiteSmall));

        header.addCell(left);
        header.addCell(right);
        document.add(header);
    }

    private void addStatementInfo(
            Document document,
            Wallet wallet,
            List<WalletTransaction> transactions,
            Font titleFont,
            Font sectionFont,
            Font normalFont,
            Font mutedFont
    ) throws DocumentException {

        Paragraph title = new Paragraph("Wallet Statement", titleFont);
        title.setSpacingAfter(12);
        document.add(title);

        String statementNumber = "STAT-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-"
                + String.format("%06d", wallet.getId());

        String generated = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));

        String periodStart = transactions.isEmpty()
                ? LocalDate.now().toString()
                : transactions.get(transactions.size() - 1).getCreatedAt().toLocalDate().toString();

        String periodEnd = transactions.isEmpty()
                ? LocalDate.now().toString()
                : transactions.get(0).getCreatedAt().toLocalDate().toString();

        PdfPTable info = new PdfPTable(4);
        info.setWidthPercentage(100);
        info.setWidths(new float[]{25, 25, 25, 25});
        info.setSpacingAfter(12);

        info.addCell(infoCell("Account Holder", fullName(wallet), sectionFont, normalFont));
        info.addCell(infoCell("Statement Number", statementNumber, sectionFont, normalFont));
        info.addCell(infoCell("Generated", generated, sectionFont, normalFont));
        info.addCell(infoCell("Currency", wallet.getCurrency(), sectionFont, normalFont));

        document.add(info);

        PdfPTable second = new PdfPTable(2);
        second.setWidthPercentage(100);
        second.setWidths(new float[]{65, 35});
        second.setSpacingAfter(14);

        second.addCell(infoCell("Email", wallet.getUser().getEmail(), sectionFont, normalFont));
        second.addCell(infoCell("Statement Period", periodStart + " → " + periodEnd, sectionFont, normalFont));

        document.add(second);

        Paragraph note = new Paragraph(
                "Official wallet statement generated from PayFlow wallet and ledger records.",
                mutedFont
        );
        note.setSpacingAfter(12);
        document.add(note);
    }

    private void addSummaryCards(
            Document document,
            Wallet wallet,
            List<WalletTransaction> transactions,
            BigDecimal openingBalance,
            BigDecimal totalCredits,
            BigDecimal totalDebits,
            Font sectionFont,
            Font normalFont,
            DecimalFormat moneyFormatter
    ) throws DocumentException {

        PdfPTable cards = new PdfPTable(5);
        cards.setWidthPercentage(100);
        cards.setWidths(new float[]{20, 20, 20, 20, 20});
        cards.setSpacingAfter(18);

        cards.addCell(infoCell("Opening Balance", money(wallet, openingBalance, moneyFormatter), sectionFont, normalFont));
        cards.addCell(infoCell("Total Credits", money(wallet, totalCredits, moneyFormatter), sectionFont, normalFont));
        cards.addCell(infoCell("Total Debits", money(wallet, totalDebits, moneyFormatter), sectionFont, normalFont));
        cards.addCell(infoCell("Transactions", String.valueOf(transactions.size()), sectionFont, normalFont));
        cards.addCell(infoCell("Closing Balance", money(wallet, wallet.getBalance(), moneyFormatter), sectionFont, normalFont));

        document.add(cards);
    }

    private void addTransactionsTable(
            Document document,
            Wallet wallet,
            List<WalletTransaction> transactions,
            BigDecimal openingBalance,
            Font normalFont,
            Font boldFont,
            DecimalFormat moneyFormatter,
            DateTimeFormatter dateFormatter
    ) throws DocumentException {

        Paragraph section = new Paragraph("Transaction History", boldFont);
        section.setSpacingAfter(8);
        document.add(section);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{15, 17, 24, 12, 12, 10, 10});
        table.setSpacingAfter(18);

        table.addCell(headerCell("Date"));
        table.addCell(headerCell("Reference"));
        table.addCell(headerCell("Description"));
        table.addCell(headerCell("Debit"));
        table.addCell(headerCell("Credit"));
        table.addCell(headerCell("Status"));
        table.addCell(headerCell("Balance"));

        BigDecimal runningBalance = openingBalance;

        for (int i = transactions.size() - 1; i >= 0; i--) {
            WalletTransaction tx = transactions.get(i);

            BigDecimal debit = isDebit(tx) ? tx.getAmount() : BigDecimal.ZERO;
            BigDecimal credit = isCredit(tx) ? tx.getAmount() : BigDecimal.ZERO;

            runningBalance = runningBalance.add(credit).subtract(debit);

            table.addCell(bodyCell(tx.getCreatedAt().format(dateFormatter), normalFont, Element.ALIGN_LEFT));
            table.addCell(bodyCell(shortReference(tx.getReference()), normalFont, Element.ALIGN_LEFT));
            table.addCell(bodyCell(cleanDescription(tx), normalFont, Element.ALIGN_LEFT));
            table.addCell(bodyCell(debit.signum() == 0 ? "-" : moneyFormatter.format(debit), normalFont, Element.ALIGN_RIGHT));
            table.addCell(bodyCell(credit.signum() == 0 ? "-" : moneyFormatter.format(credit), normalFont, Element.ALIGN_RIGHT));
            table.addCell(statusCell(tx.getStatus(), normalFont));
            table.addCell(bodyCell(moneyFormatter.format(runningBalance), normalFont, Element.ALIGN_RIGHT));
        }

        document.add(table);
    }

    private void addFooter(Document document, Font mutedFont) throws DocumentException {
        PdfPTable footer = new PdfPTable(1);
        footer.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.TOP);
        cell.setBorderColor(BORDER);
        cell.setPaddingTop(10);
        cell.addElement(centered("Generated by PayFlow OS", mutedFont));
        cell.addElement(centered("African Business Operating System", mutedFont));
        cell.addElement(centered("Digitally generated • No signature required", mutedFont));
        cell.addElement(centered("support@payflow.africa • www.payflow.africa", mutedFont));

        footer.addCell(cell);
        document.add(footer);
    }

    private BigDecimal totalDebits(List<WalletTransaction> transactions) {
        return transactions.stream()
                .filter(this::isDebit)
                .map(WalletTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal totalCredits(List<WalletTransaction> transactions) {
        return transactions.stream()
                .filter(this::isCredit)
                .map(WalletTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isDebit(WalletTransaction tx) {
        return tx.getType() == TransactionType.TRANSFER_OUT
                || tx.getType() == TransactionType.PAYMENT;
    }

    private boolean isCredit(WalletTransaction tx) {
        return tx.getType() == TransactionType.DEPOSIT
                || tx.getType() == TransactionType.TRANSFER_IN
                || tx.getType() == TransactionType.REVERSAL;
    }

    private String fullName(Wallet wallet) {
        String firstName = wallet.getUser().getFirstName() == null ? "" : wallet.getUser().getFirstName();
        String lastName = wallet.getUser().getLastName() == null ? "" : wallet.getUser().getLastName();

        String fullName = (firstName + " " + lastName).trim();

        return fullName.isBlank() ? wallet.getUser().getEmail() : fullName;
    }

    private String money(Wallet wallet, BigDecimal amount, DecimalFormat formatter) {
        return formatter.format(amount) + " " + wallet.getCurrency();
    }

    private String shortReference(String reference) {
        if (reference == null) {
            return "-";
        }

        if (reference.length() <= 22) {
            return reference;
        }

        return reference.substring(0, 18) + "...";
    }

    private String cleanDescription(WalletTransaction tx) {

    if (tx.getDescription() != null && !tx.getDescription().isBlank()) {
        return tx.getDescription();
    }

    return switch (tx.getType()) {

        case DEPOSIT ->
                "Wallet deposit";

        case WITHDRAWAL ->
                "Wallet withdrawal";

        case TRANSFER_OUT ->
                "Outgoing transfer";

        case TRANSFER_IN ->
                "Incoming transfer";

        case PAYMENT ->
                "Invoice payment";

        case REFUND ->
                "Refund";

        case REVERSAL ->
                "Transaction reversal";

        default ->
                "Wallet transaction";
    };
}

    private PdfPCell infoCell(String title, String value, Font titleFont, Font valueFont) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(10);
        cell.setBorderColor(BORDER);
        cell.setBackgroundColor(LIGHT_BG);
        cell.addElement(new Paragraph(title, titleFont));
        cell.addElement(new Paragraph(value, valueFont));
        return cell;
    }

    private PdfPCell headerCell(String text) {
        Font font = new Font(Font.HELVETICA, 8, Font.BOLD, Color.WHITE);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(DARK);
        cell.setPadding(7);
        cell.setBorderColor(DARK);
        return cell;
    }

    private PdfPCell bodyCell(String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        cell.setHorizontalAlignment(align);
        cell.setBorderColor(BORDER);
        return cell;
    }

    private PdfPCell statusCell(TransactionStatus status, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(status.name(), font));
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderColor(BORDER);

        if (status == TransactionStatus.SUCCESS) {
            cell.setBackgroundColor(GREEN_BG);
        } else if (status == TransactionStatus.PENDING) {
            cell.setBackgroundColor(ORANGE_BG);
        } else if (status == TransactionStatus.FAILED) {
            cell.setBackgroundColor(RED_BG);
        } else if (status == TransactionStatus.REVERSED) {
            cell.setBackgroundColor(BLUE_BG);
        } else {
            cell.setBackgroundColor(LIGHT_BG);
        }

        return cell;
    }

    private Paragraph centered(String text, Font font) {
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_CENTER);
        return p;
    }
}