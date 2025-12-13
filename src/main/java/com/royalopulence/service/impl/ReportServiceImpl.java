package com.royalopulence.service.impl;

import com.royalopulence.dto.report.PaymentSummaryResponse;
import com.royalopulence.model.operation.Payment;
import com.royalopulence.repository.PaymentRepository;
import com.royalopulence.service.base.ReportService;
import com.royalopulence.util.PdfUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PaymentRepository paymentRepository;
    private final PdfUtil pdfUtil;

    @Override
    public PaymentSummaryResponse getPaymentSummary() {
        double totalRevenue = paymentRepository.findAll().stream()
                .filter(p -> "SUCCESS".equalsIgnoreCase(p.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();

        long totalCount = paymentRepository.count();

        return new PaymentSummaryResponse(totalRevenue, totalCount);
    }

    @Override
    public byte[] downloadPaymentSummaryPdf() {
        PaymentSummaryResponse summary = getPaymentSummary();
        return pdfUtil.generatePaymentSummaryPdf(summary);
    }
}
