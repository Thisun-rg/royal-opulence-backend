package com.example.hotelpayment.Controller;

import com.example.hotelpayment.DTO.Common.ApiResponse;
import com.example.hotelpayment.DTO.Report.PaymentSummaryResponse;
import com.example.hotelpayment.Service.Base.ReportService;
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
}
