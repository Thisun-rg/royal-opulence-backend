package com.example.hotelpayment.Service.Impl;

import com.example.hotelpayment.DTO.Payment.PaymentRequest;
import com.example.hotelpayment.DTO.Payment.PaymentResponse;
import com.example.hotelpayment.Model.Operation.Payment;
import com.example.hotelpayment.Repository.PaymentRepository;
import com.example.hotelpayment.Service.Base.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {

        Payment payment = new Payment();
        payment.setReservationId(request.getReservationId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setStatus("PENDING");      // later updated by real gateway
        payment.setMethod("NOT_SET");      // STRIPE / PAYHERE later
        payment.setCreatedAt(System.currentTimeMillis());

        payment = paymentRepository.save(payment);

        return mapToResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentById(String id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return mapToResponse(payment);
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getReservationId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus()
        );
    }
}
