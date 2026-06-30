package com.hedi.payflow.invoice.pdf;

import com.hedi.payflow.invoice.entity.Invoice;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

@Service
public class InvoicePdfService {

    private static final Color PAYFLOW_BLUE = new Color(37, 99, 235);
    private static final Color DARK = new Color(15, 23, 42);
    private static final Color LIGHT_BG = new Color(248, 250, 252);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color GREEN = new Color(22, 163, 74);
    private static final Color ORANGE = new Color(245, 158, 11);

    public byte[] generateInvoicePdf(Invoice invoice) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 42, 42, 42, 42);
            PdfWriter.getInstance(document, out);

            document.open();

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
            DecimalFormat moneyFormatter = new DecimalFormat("#,##0.00");

            Font brandFont = new Font(Font.HELVETICA, 24, Font.BOLD, Color.WHITE);
            Font whiteSmall = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.WHITE);
            Font titleFont = new Font(Font.HELVETICA, 22, Font.BOLD, DARK);
            Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD, DARK);
            Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL, DARK);
            Font mutedFont = new Font(Font.HELVETICA, 9, Font.NORMAL, new Color(100, 116, 139));
            Font boldFont = new Font(Font.HELVETICA, 10, Font.BOLD, DARK);
            Font totalFont = new Font(Font.HELVETICA, 18, Font.BOLD, DARK);

            addHeader(document, brandFont, whiteSmall);
            addInvoiceTitle(document, invoice, titleFont, mutedFont, boldFont, dateFormatter);
            addParties(document, invoice, sectionFont, normalFont, mutedFont);
            addItemsTable(document, invoice, sectionFont, normalFont, boldFont, moneyFormatter);
            addTotalBox(document, invoice.getAmount(), totalFont, mutedFont, moneyFormatter);
            addFooter(document, mutedFont);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate invoice PDF", e);
        }
    }

    private void addHeader(Document document, Font brandFont, Font whiteSmall) throws DocumentException {
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{70, 30});
        header.setSpacingAfter(28);

        PdfPCell brandCell = new PdfPCell();
        brandCell.setBackgroundColor(PAYFLOW_BLUE);
        brandCell.setPadding(18);
        brandCell.setBorder(Rectangle.NO_BORDER);

        Paragraph brand = new Paragraph("PAYFLOW", brandFont);
        Paragraph subtitle = new Paragraph("Mediterranean Fintech OS", whiteSmall);
        brandCell.addElement(brand);
        brandCell.addElement(subtitle);

        PdfPCell rightCell = new PdfPCell();
        rightCell.setBackgroundColor(PAYFLOW_BLUE);
        rightCell.setPadding(18);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        rightCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.addElement(new Paragraph("Professional Invoice", whiteSmall));

        header.addCell(brandCell);
        header.addCell(rightCell);

        document.add(header);
    }

    private void addInvoiceTitle(
            Document document,
            Invoice invoice,
            Font titleFont,
            Font mutedFont,
            Font boldFont,
            DateTimeFormatter dateFormatter
    ) throws DocumentException {

        PdfPTable top = new PdfPTable(2);
        top.setWidthPercentage(100);
        top.setWidths(new float[]{65, 35});
        top.setSpacingAfter(22);

        PdfPCell left = cleanCell();
        Paragraph title = new Paragraph("Invoice", titleFont);
        left.addElement(title);
        left.addElement(new Paragraph("Invoice Number: " + invoice.getInvoiceNumber(), mutedFont));
        left.addElement(new Paragraph("Issue Date: " + invoice.getCreatedAt().format(dateFormatter), mutedFont));

        if (invoice.getPaidAt() != null) {
            left.addElement(new Paragraph("Payment Date: " + invoice.getPaidAt().format(dateFormatter), mutedFont));
        }

        PdfPCell right = cleanCell();
        right.setHorizontalAlignment(Element.ALIGN_RIGHT);

        PdfPTable badge = new PdfPTable(1);
        badge.setWidthPercentage(55);
        badge.setHorizontalAlignment(Element.ALIGN_RIGHT);

        PdfPCell badgeCell = new PdfPCell(new Phrase(invoice.getStatus().name(), boldFont));
        badgeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        badgeCell.setPadding(9);
        badgeCell.setBorder(Rectangle.NO_BORDER);
        badgeCell.setBackgroundColor(
                invoice.getStatus().name().equals("PAID")
                        ? new Color(220, 252, 231)
                        : new Color(254, 243, 199)
        );

        badge.addCell(badgeCell);
        right.addElement(badge);

        top.addCell(left);
        top.addCell(right);

        document.add(top);
    }

    private void addParties(
            Document document,
            Invoice invoice,
            Font sectionFont,
            Font normalFont,
            Font mutedFont
    ) throws DocumentException {

        PdfPTable parties = new PdfPTable(2);
        parties.setWidthPercentage(100);
        parties.setWidths(new float[]{50, 50});
        parties.setSpacingAfter(22);

        PdfPCell merchant = panelCell();
        merchant.addElement(new Paragraph("Merchant", sectionFont));
        merchant.addElement(new Paragraph(" "));
        merchant.addElement(new Paragraph(
                invoice.getMerchant().getFirstName() + " " + invoice.getMerchant().getLastName(),
                normalFont
        ));
        merchant.addElement(new Paragraph(invoice.getMerchant().getEmail(), mutedFont));

        PdfPCell customer = panelCell();
        customer.addElement(new Paragraph("Customer", sectionFont));
        customer.addElement(new Paragraph(" "));
        customer.addElement(new Paragraph(invoice.getCustomerEmail(), normalFont));
        customer.addElement(new Paragraph("PayFlow Wallet Customer", mutedFont));

        parties.addCell(merchant);
        parties.addCell(customer);

        document.add(parties);
    }

    private void addItemsTable(
            Document document,
            Invoice invoice,
            Font sectionFont,
            Font normalFont,
            Font boldFont,
            DecimalFormat moneyFormatter
    ) throws DocumentException {

        Paragraph section = new Paragraph("Invoice Items", sectionFont);
        section.setSpacingAfter(8);
        document.add(section);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{60, 15, 25});
        table.setSpacingAfter(18);

        table.addCell(headerCell("Description"));
        table.addCell(headerCell("Qty"));
        table.addCell(headerCell("Amount"));

        table.addCell(bodyCell(invoice.getDescription(), normalFont, Element.ALIGN_LEFT));
        table.addCell(bodyCell("1", normalFont, Element.ALIGN_CENTER));
        table.addCell(bodyCell(moneyFormatter.format(invoice.getAmount()) + " TND", boldFont, Element.ALIGN_RIGHT));

        document.add(table);
    }

    private void addTotalBox(
            Document document,
            BigDecimal amount,
            Font totalFont,
            Font mutedFont,
            DecimalFormat moneyFormatter
    ) throws DocumentException {

        PdfPTable total = new PdfPTable(2);
        total.setWidthPercentage(45);
        total.setHorizontalAlignment(Element.ALIGN_RIGHT);
        total.setWidths(new float[]{45, 55});
        total.setSpacingAfter(30);

        PdfPCell label = new PdfPCell(new Phrase("Total", mutedFont));
        label.setPadding(12);
        label.setBorderColor(BORDER);
        label.setBackgroundColor(LIGHT_BG);

        PdfPCell value = new PdfPCell(new Phrase(moneyFormatter.format(amount) + " TND", totalFont));
        value.setPadding(12);
        value.setHorizontalAlignment(Element.ALIGN_RIGHT);
        value.setBorderColor(BORDER);
        value.setBackgroundColor(LIGHT_BG);

        total.addCell(label);
        total.addCell(value);

        document.add(total);
    }

    private void addFooter(Document document, Font mutedFont) throws DocumentException {
        Paragraph line = new Paragraph(" ");
        line.setSpacingBefore(30);
        document.add(line);

        PdfPTable footer = new PdfPTable(1);
        footer.setWidthPercentage(100);

        PdfPCell footerCell = new PdfPCell();
        footerCell.setBorder(Rectangle.TOP);
        footerCell.setBorderColor(BORDER);
        footerCell.setPaddingTop(14);
        footerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        footerCell.addElement(centered("Thank you for choosing PayFlow.", mutedFont));
        footerCell.addElement(centered("Generated by PayFlow • Modern Payment Platform", mutedFont));

        footer.addCell(footerCell);
        document.add(footer);
    }

    private PdfPCell cleanCell() {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(0);
        return cell;
    }

    private PdfPCell panelCell() {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(14);
        cell.setBorderColor(BORDER);
        cell.setBackgroundColor(LIGHT_BG);
        return cell;
    }

    private PdfPCell headerCell(String text) {
        Font font = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(DARK);
        cell.setPadding(10);
        cell.setBorderColor(DARK);
        return cell;
    }

    private PdfPCell bodyCell(String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(12);
        cell.setHorizontalAlignment(align);
        cell.setBorderColor(BORDER);
        return cell;
    }

    private Paragraph centered(String text, Font font) {
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        return paragraph;
    }
}