package com.example.hotelpayment.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hotelpayment.DTO.Report.PaymentSummaryResponse;
import com.example.hotelpayment.Service.Base.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/payments-summary")
    public ResponseEntity<PaymentSummaryResponse> getPaymentsSummary() {
        Double totalRevenue = reportService.getTotalRevenue();
        Long totalCount = reportService.getTotalPaymentCount();

        PaymentSummaryResponse response =
                new PaymentSummaryResponse(totalRevenue, totalCount);

        return ResponseEntity.ok(response);
    }
}
