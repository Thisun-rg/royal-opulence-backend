package com.royalopulence.service.impl;

import com.royalopulence.dto.payment.InvoiceRequest;
import com.royalopulence.dto.payment.PaymentRequest;
import com.royalopulence.dto.payment.PaymentResponse;
import com.royalopulence.exception.BusinessException;
import com.royalopulence.exception.ResourceNotFoundException;
import com.royalopulence.model.operation.Payment;
import com.royalopulence.model.utility.PaymentMethod;
import com.royalopulence.model.utility.PaymentStatus;
import com.royalopulence.repository.PaymentRepository;
import com.royalopulence.service.base.InvoiceService;
import com.royalopulence.service.base.PaymentService;
import com.royalopulence.service.payment.StripePaymentService;
import com.royalopulence.util.AuditUtil;
import com.stripe.model.PaymentIntent;
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

    // ---------------------------------------------------------
    // Mapping
    // ---------------------------------------------------------
    private PaymentResponse map(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getReservationId(),
                p.getBaseAmount(),
                p.getTaxAmount(),
                p.getTotalAmount(),
                p.getCurrency(),
                p.getStatus() == null ? null : p.getStatus().name(),
                p.getMethod() == null ? null : p.getMethod().name(),
                p.getCreatedAt(),
                p.getStripeClientSecret()
        );
    }

    // ---------------------------------------------------------
    // NORMAL PAYMENT (Already Final Amount)
    // ---------------------------------------------------------
    @Override
    public PaymentResponse createPayment(PaymentRequest request) {

        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new BusinessException("Amount must be positive");
        }

        Payment payment = new Payment();
        payment.setReservationId(request.getReservationId());

        // IMPORTANT: amount already includes tax from BookingService
        payment.setBaseAmount(request.getAmount());
        payment.setTaxAmount(0.0);
        payment.setTotalAmount(request.getAmount());

        payment.setCurrency(request.getCurrency());
        payment.setDescription(request.getDescription());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setMethod(PaymentMethod.ONLINE);
        payment.setCreatedAt(System.currentTimeMillis());

        payment = paymentRepository.save(payment);
        auditUtil.log("PAYMENT_CREATED", payment.getId());

        return map(payment);
    }

    // ---------------------------------------------------------
    // STRIPE PAYMENT (Already Final Amount)
    // ---------------------------------------------------------
    @Override
    public PaymentResponse createStripePayment(PaymentRequest request) {

        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new BusinessException("Amount must be positive");
        }

        try {

            // IMPORTANT: amount already includes tax
            PaymentIntent intent = stripePaymentService.createPaymentIntent(
                    request.getAmount(),
                    request.getCurrency()
            );

            Payment payment = new Payment();
            payment.setReservationId(request.getReservationId());

            payment.setBaseAmount(request.getAmount());
            payment.setTaxAmount(0.0);
            payment.setTotalAmount(request.getAmount());

            payment.setCurrency(request.getCurrency());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setMethod(PaymentMethod.STRIPE);

            payment.setStripeIntentId(intent.getId());
            payment.setStripeClientSecret(intent.getClientSecret());
            payment.setCreatedAt(System.currentTimeMillis());

            payment = paymentRepository.save(payment);
            auditUtil.log("STRIPE_PAYMENT_CREATED", payment.getId());

            return map(payment);

        } catch (Exception e) {
            throw new BusinessException("Stripe payment failed: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------
    // MARK SUCCESS
    // ---------------------------------------------------------
    @Override
    public PaymentResponse markPaymentSuccess(String id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.PAID
                || payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new BusinessException("Invalid payment state change");
        }

        payment.setStatus(PaymentStatus.PAID);
        payment = paymentRepository.save(payment);

        // Auto-create invoice
        InvoiceRequest invoiceRequest = new InvoiceRequest();
        invoiceRequest.setReservationId(payment.getReservationId());
        invoiceRequest.setPaymentId(payment.getId());

        invoiceService.createInvoice(invoiceRequest);

        auditUtil.log("PAYMENT_PAID", id);
        return map(payment);
    }

    // ---------------------------------------------------------
    // MARK FAILED
    // ---------------------------------------------------------
    @Override
    public PaymentResponse markPaymentFailed(String id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new BusinessException("Cannot fail PAID payment");
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment = paymentRepository.save(payment);

        auditUtil.log("PAYMENT_FAILED", id);
        return map(payment);
    }

    // ---------------------------------------------------------
    // REFUND SINGLE PAYMENT
    // ---------------------------------------------------------
    @Override
    public PaymentResponse markPaymentRefunded(String id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new BusinessException("Only PAID payments can be refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment = paymentRepository.save(payment);

        auditUtil.log("PAYMENT_REFUNDED", id);
        return map(payment);
    }

    // ---------------------------------------------------------
    // GETTERS
    // ---------------------------------------------------------
    @Override
    public PaymentResponse getPaymentById(String id) {
        return paymentRepository.findById(id)
                .map(this::map)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getPaymentsByReservationId(String reservationId) {
        return paymentRepository.findByReservationId(reservationId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------
    // REFUND BY RESERVATION (Used by Booking Cancel)
    // ---------------------------------------------------------
    @Override
    public PaymentResponse refundByReservation(String reservationId, double refundAmount) {

        Payment payment = paymentRepository
                .findTopByReservationIdOrderByCreatedAtDesc(reservationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found for reservation: " + reservationId));

        if (refundAmount <= 0) {
            return map(payment);
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        Payment saved = paymentRepository.save(payment);

        auditUtil.log("PAYMENT_REFUNDED_BY_RESERVATION", saved.getId());
        return map(saved);
    }

    // ---------------------------------------------------------
    // WEBHOOK HELPERS
    // ---------------------------------------------------------
    public Payment findByStripeIntentIdOrThrow(String stripeIntentId) {
        return paymentRepository.findByStripeIntentId(stripeIntentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found for stripeIntentId: " + stripeIntentId));
    }

    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }
}
