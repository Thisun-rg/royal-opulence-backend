package com.royalopulence.service.impl;

import com.royalopulence.dto.payment.InvoiceRequest;
import com.royalopulence.dto.payment.InvoiceResponse;
import com.royalopulence.exception.ResourceNotFoundException;
import com.royalopulence.model.operation.Invoice;
import com.royalopulence.model.operation.Payment;
import com.royalopulence.repository.InvoiceRepository;
import com.royalopulence.repository.PaymentRepository;
import com.royalopulence.service.base.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public InvoiceResponse createInvoice(InvoiceRequest request) {

        // ensure referenced payment exists
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + request.getPaymentId()));

        Invoice invoice = new Invoice();
        invoice.setReservationId(request.getReservationId());
        invoice.setPaymentId(request.getPaymentId());
        invoice.setTotalAmount(request.getTotalAmount());
        invoice.setCurrency(request.getCurrency() != null ? request.getCurrency() : payment.getCurrency());

        invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
        invoice.setIssuedAt(System.currentTimeMillis());

        invoice = invoiceRepository.save(invoice);

        return mapToResponse(invoice);
    }

    @Override
    public InvoiceResponse getInvoiceById(String id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        return mapToResponse(invoice);
    }

    @Override
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getReservationId(),
                invoice.getPaymentId(),
                invoice.getTotalAmount(),
                invoice.getCurrency(),
                invoice.getInvoiceNumber(),
                invoice.getIssuedAt()
        );
    }
}

