package com.royalopulence.controller.report;

import com.royalopulence.dto.common.ApiResponse;
import com.royalopulence.dto.report.PaymentSummaryResponse;
import com.royalopulence.service.base.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/payments-summary")
    public ResponseEntity<ApiResponse<PaymentSummaryResponse>> getPaymentsSummary() {
        PaymentSummaryResponse summary = reportService.getPaymentSummary();
        return ResponseEntity.ok(new ApiResponse<>(true, "Payments summary", summary));
    }

    @GetMapping("/payments-summary/pdf")
    public ResponseEntity<byte[]> downloadPaymentSummaryPdf() {
    return ResponseEntity.ok()
            .header("Content-Type", "application/pdf")
            .header("Content-Disposition", "attachment; filename=payment-summary.pdf")
            .body(reportService.downloadPaymentSummaryPdf());
    }

}

