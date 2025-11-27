package com.example.hotelpayment.Service.Impl;

import com.example.hotelpayment.DTO.Payment.InvoiceRequest;
import com.example.hotelpayment.DTO.Payment.InvoiceResponse;
import com.example.hotelpayment.Model.Operation.Invoice;
import com.example.hotelpayment.Repository.InvoiceRepository;
import com.example.hotelpayment.Service.Base.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    @Override
public InvoiceResponse createInvoice(InvoiceRequest request) {

    Invoice invoice = new Invoice();
    invoice.setReservationId(request.getReservationId());
    invoice.setPaymentId(request.getPaymentId());
    invoice.setTotalAmount(request.getTotalAmount());

    invoice.setInvoiceNumber("INV-" + System.currentTimeMillis()); 
    invoice.setCreatedAt(String.valueOf(System.currentTimeMillis())); 

    invoice = invoiceRepository.save(invoice);

    return new InvoiceResponse(
            invoice.getId(),
            invoice.getReservationId(),
            invoice.getPaymentId(),
            invoice.getTotalAmount(),
            invoice.getInvoiceNumber(),
            invoice.getCreatedAt()
    );
}

}
