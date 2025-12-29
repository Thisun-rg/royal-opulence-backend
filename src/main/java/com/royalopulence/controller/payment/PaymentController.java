package com.royalopulence.controller.payment;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.royalopulence.dto.common.ApiResponse;
import com.royalopulence.dto.payment.InvoiceRequest;
import com.royalopulence.dto.payment.InvoiceResponse;
import com.royalopulence.dto.payment.PaymentRequest;
import com.royalopulence.dto.payment.PaymentResponse;
import com.royalopulence.service.base.InvoiceService;
import com.royalopulence.service.base.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody PaymentRequest request) {

        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment created", response));
    }

    @PostMapping("/stripe")
    public ResponseEntity<ApiResponse<PaymentResponse>> createStripePayment(
            @Valid @RequestBody PaymentRequest request) {

        PaymentResponse response = paymentService.createStripePayment(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Stripe payment created", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(@PathVariable String id) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Payment found", paymentService.getPaymentById(id))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPayments() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Payments list", paymentService.getAllPayments())
        );
    }

    @PatchMapping("/{id}/success")
    public ResponseEntity<ApiResponse<PaymentResponse>> markSuccess(@PathVariable String id) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Payment SUCCESS", paymentService.markPaymentSuccess(id))
        );
    }

    @PatchMapping("/{id}/failed")
    public ResponseEntity<ApiResponse<PaymentResponse>> markFailed(@PathVariable String id) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Payment FAILED", paymentService.markPaymentFailed(id))
        );
    }

    @PatchMapping("/{id}/refund")
    public ResponseEntity<ApiResponse<PaymentResponse>> refundPayment(@PathVariable String id) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Payment REFUNDED", paymentService.markPaymentRefunded(id))
        );
    }

    @PostMapping("/invoice")
    public ResponseEntity<ApiResponse<InvoiceResponse>> createInvoice(
            @Valid @RequestBody InvoiceRequest request) {

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Invoice created", invoiceService.createInvoice(request))
        );
    }

    // ---------------- HEALTH CHECK ----------------
    @GetMapping("/health")
    public String health() {
        return "Payment module is up";
    }
}
