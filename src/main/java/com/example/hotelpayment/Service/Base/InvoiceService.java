package com.example.hotelpayment.Service.Base;

import com.example.hotelpayment.DTO.Payment.InvoiceRequest;
import com.example.hotelpayment.DTO.Payment.InvoiceResponse;

import java.util.List;

public interface InvoiceService {

    InvoiceResponse createInvoice(InvoiceRequest request);

    InvoiceResponse getInvoiceById(String id);

    List<InvoiceResponse> getAllInvoices();
}

