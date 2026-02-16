package com.royalopulence.service.base;

import com.royalopulence.dto.report.PaymentSummaryResponse;

public interface ReportService {

    PaymentSummaryResponse getPaymentSummary();

    byte[] downloadPaymentSummaryPdf();
}
