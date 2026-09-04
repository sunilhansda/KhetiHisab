package com.example.demo.mapper;

import com.example.demo.dto.payment.PaymentAllocationResponse;
import com.example.demo.dto.payment.PaymentResponse;
import com.example.demo.entity.Payment;
import com.example.demo.repository.PaymentAllocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentMapper {

    private final PaymentAllocationRepository
            paymentAllocationRepository;

    public PaymentResponse toResponse(Payment payment) {

        var allocations =
                paymentAllocationRepository
                        .findByPaymentPaymentId(
                                payment.getPaymentId()
                        )
                        .stream()
                        .map(allocation ->
                                new PaymentAllocationResponse(
                                        allocation.getAllocationId(),
                                        allocation.getJob().getJobId(),
                                        allocation.getAmount()
                                ))
                        .toList();

        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getCustomer().getCustomerId(),
                payment.getCustomer().getName(),
                payment.getPaymentDate(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getNotes(),
                allocations,
                payment.getCreatedAt()
        );
    }
}
