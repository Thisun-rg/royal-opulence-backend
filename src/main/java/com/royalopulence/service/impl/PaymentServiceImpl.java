package com.royalopulence.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final StripePaymentService stripePaymentService;
    private final InvoiceService invoiceService;
    private final AuditUtil auditUtil;

    private static final double TAX_RATE = 0.10;

    private double total(double base) {
        return base + (base * TAX_RATE);
    }

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {

        // ⭐ NEW SAFETY CHECK
        if (request.getAmount() <= 0) {
            throw new BusinessException("Amount must be positive");
        }

        Payment payment = new Payment();
        payment.setReservationId(request.getReservationId());
        payment.setBaseAmount(request.getAmount());
        payment.setTaxAmount(request.getAmount() * TAX_RATE);
        payment.setTotalAmount(total(request.getAmount()));
        payment.setCurrency(request.getCurrency());
        payment.setDescription(request.getDescription());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setMethod(PaymentMethod.NOT_SET);
        payment.setCreatedAt(System.currentTimeMillis());

        payment = paymentRepository.save(payment);
        auditUtil.log("PAYMENT_CREATED", payment.getId());

        return map(payment);
    }

    @Override
    public PaymentResponse createStripePayment(PaymentRequest request) {

        if (request.getAmount() <= 0) {
            throw new BusinessException("Amount must be positive");
        }

        try {
            var intent = stripePaymentService.createPaymentIntent(
                    total(request.getAmount()),
                    request.getCurrency()
            );

            Payment payment = new Payment();
            payment.setReservationId(request.getReservationId());
            payment.setBaseAmount(request.getAmount());
            payment.setTaxAmount(request.getAmount() * TAX_RATE);
            payment.setTotalAmount(total(request.getAmount()));
            payment.setCurrency(request.getCurrency());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setMethod(PaymentMethod.STRIPE);
            payment.setStripeIntentId(intent.getId());
            payment.setCreatedAt(System.currentTimeMillis());
            payment.setExpiresAt(System.currentTimeMillis() + 15 * 60 * 1000);

            payment = paymentRepository.save(payment);
            auditUtil.log("STRIPE_PAYMENT_CREATED", payment.getId());

            return map(payment);

        } catch (Exception e) {
            throw new BusinessException("Stripe payment failed");
        }
    }

    @Override
    public PaymentResponse markPaymentSuccess(String id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.SUCCESS
                || payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new BusinessException("Invalid payment state change");
        }

        if (payment.getExpiresAt() != null
                && System.currentTimeMillis() > payment.getExpiresAt()) {
            throw new BusinessException("Payment expired");
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        payment = paymentRepository.save(payment);

        InvoiceRequest invoiceRequest = new InvoiceRequest();
        invoiceRequest.setReservationId(payment.getReservationId());
        invoiceRequest.setPaymentId(payment.getId());
        invoiceRequest.setTotalAmount(payment.getTotalAmount());
        invoiceRequest.setCurrency(payment.getCurrency());

        invoiceService.createInvoice(invoiceRequest);
        auditUtil.log("PAYMENT_SUCCESS", id);

        return map(payment);
    }

    @Override
    public PaymentResponse markPaymentFailed(String id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new BusinessException("Cannot fail SUCCESS payment");
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment = paymentRepository.save(payment);
        auditUtil.log("PAYMENT_FAILED", id);

        return map(payment);
    }

    @Override
    public PaymentResponse markPaymentRefunded(String id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new BusinessException("Only SUCCESS payments can be refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment = paymentRepository.save(payment);
        auditUtil.log("PAYMENT_REFUNDED", id);

        return map(payment);
    }

    @Override
    public PaymentResponse getPaymentById(String id) {
        return paymentRepository.findById(id)
                .map(this::map)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getPaymentsByReservationId(String reservationId) {
        return paymentRepository.findByReservationId(reservationId)
                .stream().map(this::map).collect(Collectors.toList());
    }

    private PaymentResponse map(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getReservationId(),
                p.getBaseAmount(),
                p.getTaxAmount(),
                p.getTotalAmount(),
                p.getCurrency(),
                p.getStatus().name(),
                p.getMethod().name(),
                p.getCreatedAt()
        );
    }
}
