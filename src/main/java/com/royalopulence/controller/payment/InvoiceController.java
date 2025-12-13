package com.royalopulence.controller.payment;

import com.royalopulence.dto.payment.InvoiceRequest;
import com.royalopulence.dto.payment.InvoiceResponse;
import com.royalopulence.service.base.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    // CREATE invoice
    @PostMapping
    public ResponseEntity<InvoiceResponse> createInvoice(
            @RequestBody InvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.createInvoice(request));
    }

    // GET all invoices
    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    // GET invoice by invoiceId
    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceResponse> getInvoiceById(
            @PathVariable String invoiceId) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(invoiceId));
    }

    // GET invoice by paymentId
    @GetMapping("/by-payment/{paymentId}")
    public ResponseEntity<InvoiceResponse> getInvoiceByPaymentId(
            @PathVariable String paymentId) {
        return ResponseEntity.ok(invoiceService.getInvoiceByPaymentId(paymentId));
    }

    // GET invoices by reservationId
    @GetMapping("/by-reservation/{reservationId}")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByReservationId(
            @PathVariable String reservationId) {
        return ResponseEntity.ok(
                invoiceService.getInvoicesByReservationId(reservationId)
        );
    }

    // DOWNLOAD invoice PDF
    @GetMapping("/{invoiceId}/pdf")
    public ResponseEntity<byte[]> downloadInvoicePdf(
            @PathVariable String invoiceId) {

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=invoice.pdf")
                .body(invoiceService.downloadInvoicePdf(invoiceId));
    }
}
