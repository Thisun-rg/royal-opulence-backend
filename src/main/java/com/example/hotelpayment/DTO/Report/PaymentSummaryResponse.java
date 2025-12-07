package com.example.hotelpayment.DTO.Report;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentSummaryResponse {
    private Double totalRevenue;
    private Long totalPayments;
}

