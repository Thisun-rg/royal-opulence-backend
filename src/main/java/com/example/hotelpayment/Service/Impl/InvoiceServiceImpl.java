package com.example.hotelpayment.Service.Impl;

import com.example.hotelpayment.DTO.Payment.InvoiceRequest;
import com.example.hotelpayment.DTO.Payment.InvoiceResponse;
import com.example.hotelpayment.Exception.ResourceNotFoundException;
import com.example.hotelpayment.Model.Operation.Invoice;
import com.example.hotelpayment.Model.Operation.Payment;
import com.example.hotelpayment.Repository.InvoiceRepository;
import com.example.hotelpayment.Repository.PaymentRepository;
import com.example.hotelpayment.Service.Base.InvoiceService;
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

        // optional: ensure payment exists & is SUCCESS later
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
        return invoiceRepository.findAll()
                .stream()
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

