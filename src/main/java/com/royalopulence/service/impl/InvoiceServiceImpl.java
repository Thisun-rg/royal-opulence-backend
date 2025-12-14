package com.royalopulence.service.impl;

import com.royalopulence.dto.payment.InvoiceRequest;
import com.royalopulence.dto.payment.InvoiceResponse;
import com.royalopulence.exception.BusinessException;
import com.royalopulence.exception.ResourceNotFoundException;
import com.royalopulence.model.operation.Invoice;
import com.royalopulence.model.operation.Payment;
import com.royalopulence.repository.InvoiceRepository;
import com.royalopulence.repository.PaymentRepository;
import com.royalopulence.service.base.InvoiceService;
import com.royalopulence.util.PdfUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final PdfUtil pdfUtil;

    @Override
    public InvoiceResponse createInvoice(InvoiceRequest request) {

        // ---------------- DUPLICATE CHECK ADDED HERE ----------------
        invoiceRepository.findByPaymentId(request.getPaymentId())
                .ifPresent(i -> {
                    throw new BusinessException("Invoice already exists for this payment" + i.getInvoiceNumber());
                });
        // ------------------------------------------------------------

        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(()
                        -> new ResourceNotFoundException(
                        "Payment not found with id: " + request.getPaymentId())
                );

        Invoice invoice = new Invoice();
        invoice.setReservationId(request.getReservationId());
        invoice.setPaymentId(request.getPaymentId());
        invoice.setTotalAmount(request.getTotalAmount());
        invoice.setCurrency(
                request.getCurrency() != null ? request.getCurrency() : payment.getCurrency()
        );
        invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
        invoice.setIssuedAt(System.currentTimeMillis());

        invoice = invoiceRepository.save(invoice);
        return mapToResponse(invoice);
    }

    @Override
    public InvoiceResponse getInvoiceById(String id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Invoice not found with id: " + id)
                );
        return mapToResponse(invoice);
    }

    @Override
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public InvoiceResponse getInvoiceByPaymentId(String paymentId) {
        Invoice invoice = invoiceRepository.findByPaymentId(paymentId)
                .orElseThrow(()
                        -> new ResourceNotFoundException(
                        "Invoice not found for paymentId: " + paymentId)
                );
        return mapToResponse(invoice);
    }

    @Override
    public List<InvoiceResponse> getInvoicesByReservationId(String reservationId) {
        return invoiceRepository.findByReservationId(reservationId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public byte[] downloadInvoicePdf(String invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(()
                        -> new ResourceNotFoundException(
                        "Invoice not found with id: " + invoiceId)
                );

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
