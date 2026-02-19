package com.royalopulence.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentSummaryResponse {
    private Double totalRevenue;
    private Long totalPayments;
}

