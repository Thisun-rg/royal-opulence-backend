package com.royalopulence.util;

import com.royalopulence.dto.report.PaymentSummaryResponse;
import com.royalopulence.model.operation.Invoice;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class PdfUtil {

    public byte[] generateInvoicePdf(Invoice invoice) {
        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.setLeading(20f);
            content.newLineAtOffset(50, 700);

            content.showText("Invoice");
            content.newLine();
            content.showText("Invoice Number: " + invoice.getInvoiceNumber());
            content.newLine();
            content.showText("Payment ID: " + invoice.getPaymentId());
            content.newLine();
            content.showText("Reservation ID: " + invoice.getReservationId());
            content.newLine();
            content.showText("Amount: " + invoice.getTotalAmount() + " " + invoice.getCurrency());

            content.endText();
            content.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate invoice PDF", e);
        }
    }

    // 👇 REQUIRED for reports
    public byte[] generatePaymentSummaryPdf(PaymentSummaryResponse summary) {
        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.setLeading(20f);
            content.newLineAtOffset(50, 700);

            content.showText("Payment Summary Report");
            content.newLine();
            content.showText("Total Revenue: " + summary.getTotalRevenue());
            content.newLine();
            content.showText("Total Payments: " + summary.getTotalPayments());

            content.endText();
            content.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate payment summary PDF", e);
        }
    }
}
