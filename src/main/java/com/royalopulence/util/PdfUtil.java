package com.royalopulence.util;

import com.royalopulence.model.payment.Invoice;
import com.royalopulence.dto.report.PaymentSummaryResponse;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class PdfUtil {

    public byte[] generateInvoicePdf(Invoice invoice) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);
            content.setFont(PDType1Font.HELVETICA_BOLD, 16);
            content.beginText();
            content.newLineAtOffset(50, 750);
            content.showText("Royal Opulence Hotel - Invoice");
            content.endText();

            content.setFont(PDType1Font.HELVETICA, 12);
            content.beginText();
            content.newLineAtOffset(50, 700);
            content.showText("Invoice ID: " + invoice.getInvoiceId());
            content.newLineAtOffset(0, -20);
            content.showText("Payment ID: " + invoice.getPayment().getPaymentId());
            content.newLineAtOffset(0, -20);
            content.showText("Reservation ID: " + invoice.getReservationId());
            content.newLineAtOffset(0, -20);
            content.showText("Amount: " + invoice.getAmount());
            content.newLineAtOffset(0, -20);
            content.showText("Currency: " + invoice.getCurrency());
            content.newLineAtOffset(0, -20);
            content.showText("Issued Date: " + invoice.getIssuedDate());
            content.endText();

            content.close();
            document.save(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate invoice PDF", e);
        }
    }

    public byte[] generatePaymentSummaryPdf(PaymentSummaryResponse summary) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);
            content.setFont(PDType1Font.HELVETICA_BOLD, 16);
            content.beginText();
            content.newLineAtOffset(50, 750);
            content.showText("Payment Summary Report");
            content.endText();

            content.setFont(PDType1Font.HELVETICA, 12);
            content.beginText();
            content.newLineAtOffset(50, 700);
            content.showText("Total Revenue: " + summary.getTotalRevenue());
            content.newLineAtOffset(0, -20);
            content.showText("Successful Payments: " + summary.getSuccessfulPayments());
            content.newLineAtOffset(0, -20);
            content.showText("Generated Date: " + summary.getGeneratedDate());
            content.endText();

            content.close();
            document.save(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate payment summary PDF", e);
        }
    }
}
