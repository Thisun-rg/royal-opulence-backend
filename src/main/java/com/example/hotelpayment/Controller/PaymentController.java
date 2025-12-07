package com.example.hotelpayment.Controller;

import com.example.hotelpayment.DTO.Common.ApiResponse;
import com.example.hotelpayment.DTO.Payment.InvoiceRequest;
import com.example.hotelpayment.DTO.Payment.InvoiceResponse;
import com.example.hotelpayment.DTO.Payment.PaymentRequest;
import com.example.hotelpayment.DTO.Payment.PaymentResponse;
import com.example.hotelpayment.Service.Base.InvoiceService;
import com.example.hotelpayment.Service.Base.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment created", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(@PathVariable String id) {
        PaymentResponse response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment found", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPayments() {
        List<PaymentResponse> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(new ApiResponse<>(true, "Payments list", payments));
    }

    @GetMapping("/by-reservation/{reservationId}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getByReservation(@PathVariable String reservationId) {
        List<PaymentResponse> payments = paymentService.getPaymentsByReservationId(reservationId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payments for reservation", payments));
    }

    @PatchMapping("/{id}/success")
    public ResponseEntity<ApiResponse<PaymentResponse>> markSuccess(@PathVariable String id) {
        PaymentResponse response = paymentService.markPaymentSuccess(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment marked as SUCCESS", response));
    }

    @PatchMapping("/{id}/failed")
    public ResponseEntity<ApiResponse<PaymentResponse>> markFailed(@PathVariable String id) {
        PaymentResponse response = paymentService.markPaymentFailed(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment marked as FAILED", response));
    }

    @PostMapping("/invoice")
    public ResponseEntity<ApiResponse<InvoiceResponse>> createInvoice(@Valid @RequestBody InvoiceRequest request) {
        InvoiceResponse response = invoiceService.createInvoice(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Invoice created", response));
    }
}
