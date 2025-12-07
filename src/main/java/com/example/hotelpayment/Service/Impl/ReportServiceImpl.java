package com.example.hotelpayment.Service.Impl;

import com.example.hotelpayment.DTO.Report.PaymentSummaryResponse;
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
    public PaymentSummaryResponse getPaymentSummary() {
        double totalRevenue = paymentRepository.findAll().stream()
                .filter(p -> "SUCCESS".equalsIgnoreCase(p.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();

        long totalCount = paymentRepository.count();

        return new PaymentSummaryResponse(totalRevenue, totalCount);
    }
}

