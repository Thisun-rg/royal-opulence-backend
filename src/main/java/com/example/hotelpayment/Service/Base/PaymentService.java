package com.example.hotelpayment.Service.Base;

import com.example.hotelpayment.DTO.Payment.PaymentRequest;
import com.example.hotelpayment.DTO.Payment.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest request);
    PaymentResponse getPaymentById(String id);
    List<PaymentResponse> getAllPayments();
}
