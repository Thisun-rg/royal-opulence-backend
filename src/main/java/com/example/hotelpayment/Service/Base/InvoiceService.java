package com.example.hotelpayment.Service.Base;

import com.example.hotelpayment.DTO.Payment.InvoiceRequest;
import com.example.hotelpayment.DTO.Payment.InvoiceResponse;

public interface InvoiceService {
    InvoiceResponse createInvoice(InvoiceRequest request);
}
