package com.example.hotelpayment.Service.Impl;

import com.example.hotelpayment.DTO.Payment.PaymentRequest;
import com.example.hotelpayment.DTO.Payment.PaymentResponse;
import com.example.hotelpayment.Exception.ResourceNotFoundException;
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
        payment.setDescription(request.getDescription());

        payment.setStatus("PENDING");      // will update after gateway callback
        payment.setMethod("NOT_SET");      // STRIPE / PAYHERE later
        payment.setCreatedAt(System.currentTimeMillis());

        payment = paymentRepository.save(payment);

        return mapToResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentById(String id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
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
    public PaymentResponse markPaymentSuccess(String id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        payment.setStatus("SUCCESS");
        payment = paymentRepository.save(payment);
        return mapToResponse(payment);
    }

    @Override
    public PaymentResponse markPaymentFailed(String id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        payment.setStatus("FAILED");
        payment = paymentRepository.save(payment);
        return mapToResponse(payment);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getReservationId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getMethod(),
                payment.getCreatedAt()
        );
    }
}

