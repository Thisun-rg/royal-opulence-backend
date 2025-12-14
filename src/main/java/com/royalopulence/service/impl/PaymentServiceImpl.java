package com.royalopulence.service.impl;

import com.royalopulence.dto.payment.InvoiceRequest;
import com.royalopulence.dto.payment.PaymentRequest;
import com.royalopulence.dto.payment.PaymentResponse;
import com.royalopulence.exception.BusinessException;
import com.royalopulence.exception.ResourceNotFoundException;
import com.royalopulence.model.operation.Payment;
import com.royalopulence.repository.PaymentRepository;
import com.royalopulence.service.base.InvoiceService;
import com.royalopulence.service.base.PaymentService;
import com.royalopulence.service.payment.StripePaymentService;
import com.royalopulence.util.AuditUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final StripePaymentService stripePaymentService;
    private final InvoiceService invoiceService;
    private final AuditUtil auditUtil;

    private static final double TAX_RATE = 0.10;

    private double calculateTotal(double baseAmount) {
        return baseAmount + (baseAmount * TAX_RATE);
    }

    // ---------------- CREATE NORMAL PAYMENT ----------------
    @Override
    public PaymentResponse createPayment(PaymentRequest request) {

        double baseAmount = request.getAmount();
        double totalAmount = calculateTotal(baseAmount);
        double taxAmount = totalAmount - baseAmount;

        Payment payment = new Payment();
        payment.setReservationId(request.getReservationId());
        payment.setBaseAmount(baseAmount);
        payment.setTaxAmount(taxAmount);
        payment.setTotalAmount(totalAmount);
        payment.setCurrency(request.getCurrency());
        payment.setDescription(request.getDescription());
        payment.setStatus("PENDING");
        payment.setMethod("NOT_SET");
        payment.setCreatedAt(System.currentTimeMillis());

        payment = paymentRepository.save(payment);

        auditUtil.log("PAYMENT_CREATED", payment.getId());
        return mapToResponse(payment);
    }

    // ---------------- CREATE STRIPE PAYMENT ----------------
    @Override
    public PaymentResponse createStripePayment(PaymentRequest request) {

        double baseAmount = request.getAmount();
        double taxAmount = baseAmount * TAX_RATE;
        double totalAmount = baseAmount + taxAmount;

        try {
            var intent = stripePaymentService
                    .createPaymentIntent(totalAmount, request.getCurrency());

            Payment payment = new Payment();
            payment.setReservationId(request.getReservationId());
            payment.setBaseAmount(baseAmount);
            payment.setTaxAmount(taxAmount);
            payment.setTotalAmount(totalAmount);
            payment.setCurrency(request.getCurrency());
            payment.setStatus("PENDING");
            payment.setMethod("STRIPE");
            payment.setStripeIntentId(intent.getId());
            payment.setCreatedAt(System.currentTimeMillis());
            payment.setExpiresAt(System.currentTimeMillis() + (15 * 60 * 1000));

            payment = paymentRepository.save(payment);

            auditUtil.log("STRIPE_PAYMENT_CREATED", payment.getId());
            return mapToResponse(payment);

        } catch (Exception e) {
            throw new BusinessException("Stripe payment failed");
        }
    }

    // ---------------- MARK PAYMENT SUCCESS ----------------
    @Override
    public PaymentResponse markPaymentSuccess(String paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Payment not found: " + paymentId));

        // ✅ STATUS GUARD
        if ("SUCCESS".equals(payment.getStatus())) {
            throw new BusinessException("Payment already marked as SUCCESS");
        }

        // ✅ EXPIRY CHECK
        if (payment.getExpiresAt() != null
                && System.currentTimeMillis() > payment.getExpiresAt()) {
            throw new BusinessException("Payment has expired");
        }

        payment.setStatus("SUCCESS");
        payment = paymentRepository.save(payment);

        InvoiceRequest invoiceRequest = new InvoiceRequest();
        invoiceRequest.setReservationId(payment.getReservationId());
        invoiceRequest.setPaymentId(payment.getId());
        invoiceRequest.setTotalAmount(payment.getTotalAmount());
        invoiceRequest.setCurrency(payment.getCurrency());

        invoiceService.createInvoice(invoiceRequest);

        auditUtil.log("PAYMENT_SUCCESS", paymentId);
        return mapToResponse(payment);
    }

    // ---------------- MARK PAYMENT FAILED ----------------
    @Override
    public PaymentResponse markPaymentFailed(String paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Payment not found: " + paymentId));

        if ("SUCCESS".equals(payment.getStatus())) {
            throw new BusinessException("Cannot mark SUCCESS payment as FAILED");
        }

        payment.setStatus("FAILED");
        payment = paymentRepository.save(payment);

        auditUtil.log("PAYMENT_FAILED", paymentId);
        return mapToResponse(payment);
    }

    // ---------------- MARK PAYMENT REFUNDED ----------------
    @Override
    public PaymentResponse markPaymentRefunded(String paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Payment not found: " + paymentId));

        // ✅ STATUS GUARD
        if (!"SUCCESS".equals(payment.getStatus())) {
            throw new BusinessException("Only SUCCESS payments can be refunded");
        }

        payment.setStatus("REFUNDED");
        payment = paymentRepository.save(payment);

        auditUtil.log("PAYMENT_REFUNDED", paymentId);
        return mapToResponse(payment);
    }

    // ---------------- GET PAYMENT BY ID ----------------
    @Override
    public PaymentResponse getPaymentById(String paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Payment not found: " + paymentId));

        return mapToResponse(payment);
    }

    // ---------------- GET ALL PAYMENTS ----------------
    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ---------------- GET PAYMENTS BY RESERVATION ----------------
    @Override
    public List<PaymentResponse> getPaymentsByReservationId(String reservationId) {
        return paymentRepository.findByReservationId(reservationId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ---------------- MAPPER ----------------
    private PaymentResponse mapToResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getReservationId(),
                payment.getBaseAmount(),
                payment.getTaxAmount(),
                payment.getTotalAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getMethod(),
                payment.getCreatedAt()
        );
    }
}
