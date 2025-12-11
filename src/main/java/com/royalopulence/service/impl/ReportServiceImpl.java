package com.royalopulence.service.impl;

import com.royalopulence.dto.report.PaymentSummaryResponse;
import com.royalopulence.model.operation.Payment;
import com.royalopulence.repository.PaymentRepository;
import com.royalopulence.service.base.ReportService;
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
