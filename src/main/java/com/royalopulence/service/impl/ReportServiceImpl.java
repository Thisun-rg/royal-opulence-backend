package com.royalopulence.service.impl;

import org.springframework.stereotype.Service;

import com.royalopulence.dto.report.PaymentSummaryResponse;
import com.royalopulence.model.operation.Payment;
import com.royalopulence.model.utility.PaymentStatus;
import com.royalopulence.repository.PaymentRepository;
import com.royalopulence.service.base.ReportService;
import com.royalopulence.util.PdfUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PaymentRepository paymentRepository;
    private final PdfUtil pdfUtil;

    @Override
    public PaymentSummaryResponse getPaymentSummary() {

        double revenue = paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .mapToDouble(Payment::getTotalAmount)
                .sum();

        long count = paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .count();

        return new PaymentSummaryResponse(revenue, count);
    }

    @Override
    public byte[] downloadPaymentSummaryPdf() {
        return pdfUtil.generatePaymentSummaryPdf(getPaymentSummary());
    }
}
