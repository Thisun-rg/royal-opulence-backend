package com.royalopulence.service.base;

import com.royalopulence.dto.payment.InvoiceRequest;
import com.royalopulence.dto.payment.InvoiceResponse;

import java.util.List;

public interface InvoiceService {
    InvoiceResponse createInvoice(InvoiceRequest request);

    InvoiceResponse getInvoiceById(String id);

    List<InvoiceResponse> getAllInvoices();
}

