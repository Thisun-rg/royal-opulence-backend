package com.royalopulence.service.impl;

import com.royalopulence.dto.payment.PaymentRequest;
import com.royalopulence.dto.payment.PaymentResponse;
import com.royalopulence.exception.ResourceNotFoundException;
import com.royalopulence.model.operation.Payment;
import com.royalopulence.repository.PaymentRepository;
import com.royalopulence.service.base.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    // 🔹 Explicit tax rate (Supervisor likes this)
    private static final double TAX_RATE = 0.10; // 10%

    // 🔹 Tax calculation logic
    private double calculateTotal(double baseAmount) {
        double tax = baseAmount * TAX_RATE;
        return baseAmount + tax;
    }

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {

        double baseAmount = request.getAmount();
        double totalAmount = calculateTotal(baseAmount);
        double taxAmount = totalAmount - baseAmount;

        Payment payment = new Payment();
        payment.setReservationId(request.getReservationId());

        // 🔹 Clear financial fields
        payment.setBaseAmount(baseAmount);
        payment.setTaxAmount(taxAmount);
        payment.setTotalAmount(totalAmount);

        payment.setCurrency(request.getCurrency());
        payment.setDescription(request.getDescription());

        payment.setStatus("PENDING");
        payment.setMethod("NOT_SET");
        payment.setCreatedAt(System.currentTimeMillis());

        payment = paymentRepository.save(payment);

        return mapToResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentById(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found with paymentId: " + paymentId)
                );
        return mapToResponse(payment);
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getPaymentsByReservationId(String reservationId) {
        return paymentRepository.findByReservationId(reservationId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentResponse markPaymentSuccess(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found with paymentId: " + paymentId)
                );
        payment.setStatus("SUCCESS");
        payment = paymentRepository.save(payment);
        return mapToResponse(payment);
    }

    @Override
    public PaymentResponse markPaymentFailed(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found with paymentId: " + paymentId)
                );
        payment.setStatus("FAILED");
        payment = paymentRepository.save(payment);
        return mapToResponse(payment);
    }

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
