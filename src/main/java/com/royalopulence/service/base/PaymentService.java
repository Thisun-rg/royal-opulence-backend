package com.royalopulence.service.base;

import com.royalopulence.dto.payment.PaymentRequest;
import com.royalopulence.dto.payment.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse createPayment(PaymentRequest request);

    PaymentResponse getPaymentById(String id);

    List<PaymentResponse> getAllPayments();

    List<PaymentResponse> getPaymentsByReservationId(String reservationId);

    PaymentResponse markPaymentSuccess(String id);

    PaymentResponse markPaymentFailed(String id);

    PaymentResponse createStripePayment(PaymentRequest request);

    PaymentResponse markPaymentRefunded(String paymentId);

    PaymentResponse refundByReservation(String reservationId, double refundAmount);

}
