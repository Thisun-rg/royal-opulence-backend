package com.example.hotelpayment.Service.Impl;

import com.example.hotelpayment.Model.Operation.Payment;
import com.example.hotelpayment.Repository.PaymentRepository;
import com.example.hotelpayment.Service.Base.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PaymentRepository paymentRepository;

    @Override
    public Double getTotalRevenue() {
        return paymentRepository.findAll().stream()
                .filter(p -> "SUCCESS".equals(p.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    @Override
    public Long getTotalPaymentCount() {
        return paymentRepository.count();
    }
}
