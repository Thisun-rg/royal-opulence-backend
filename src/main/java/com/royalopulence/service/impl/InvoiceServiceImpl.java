package com.royalopulence.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.royalopulence.dto.payment.InvoiceRequest;
import com.royalopulence.dto.payment.InvoiceResponse;
import com.royalopulence.exception.BusinessException;
import com.royalopulence.exception.ResourceNotFoundException;
import com.royalopulence.model.operation.Invoice;
import com.royalopulence.model.operation.Payment;
import com.royalopulence.model.utility.PaymentStatus;
import com.royalopulence.repository.InvoiceRepository;
import com.royalopulence.repository.PaymentRepository;
import com.royalopulence.service.base.InvoiceService;
import com.royalopulence.util.PdfUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final PdfUtil pdfUtil;

    @Override
    public InvoiceResponse createInvoice(InvoiceRequest request) {

        invoiceRepository.findByPaymentId(request.getPaymentId())
                .ifPresent(i -> {
                    throw new BusinessException(
                            "Invoice already exists: " + i.getInvoiceNumber());
                });

        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found"));

        // ENUM-SAFE RULE
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new BusinessException(
                    "Invoice allowed only for SUCCESS payments");
        }

        Invoice invoice = new Invoice();
        invoice.setReservationId(request.getReservationId());
        invoice.setPaymentId(request.getPaymentId());
        invoice.setTotalAmount(payment.getTotalAmount());
        invoice.setCurrency(payment.getCurrency());
        invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
        invoice.setIssuedAt(System.currentTimeMillis());

        return mapToResponse(invoiceRepository.save(invoice));
    }

    @Override
    public InvoiceResponse getInvoiceById(String id) {
        return invoiceRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invoice not found"));
    }

    @Override
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll()
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public InvoiceResponse getInvoiceByPaymentId(String paymentId) {
        return invoiceRepository.findByPaymentId(paymentId)
                .map(this::mapToResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invoice not found"));
    }

    @Override
    public List<InvoiceResponse> getInvoicesByReservationId(String reservationId) {
        return invoiceRepository.findByReservationId(reservationId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public byte[] downloadInvoicePdf(String invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invoice not found"));
        return pdfUtil.generateInvoicePdf(invoice);
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
