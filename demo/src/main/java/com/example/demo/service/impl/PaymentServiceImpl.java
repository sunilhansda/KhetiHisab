package com.example.demo.service.impl;

import com.example.demo.dto.payment.*;
import com.example.demo.entity.CultivationJob;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Payment;
import com.example.demo.entity.PaymentAllocation;
import com.example.demo.exception.CultivationJobNotFoundException;
import com.example.demo.exception.CustomerNotFoundException;
import com.example.demo.exception.PaymentAllocationException;
import com.example.demo.exception.PaymentNotFoundException;
import com.example.demo.repository.CultivationJobRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.PaymentAllocationRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentAllocationRepository
            paymentAllocationRepository;
    private final CustomerRepository customerRepository;
    private final CultivationJobRepository
            cultivationJobRepository;

    @Override
    @Transactional
    public PaymentResponse createPayment(
            CreatePaymentRequest request) {

        Customer customer = customerRepository
                .findById(request.customerId())
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                request.customerId()
                        ));

        validateTotalAllocation(request);

        Payment payment = Payment.builder()
                .customer(customer)
                .paymentDate(request.paymentDate())
                .amount(request.amount())
                .paymentMethod(request.paymentMethod())
                .notes(request.notes())
                .build();

        Payment savedPayment =
                paymentRepository.save(payment);

        for (PaymentAllocationRequest allocationRequest
                : request.allocations()) {

            createAllocation(
                    savedPayment,
                    customer,
                    allocationRequest
            );
        }

        return mapToResponse(savedPayment);
    }

    private void validateTotalAllocation(
            CreatePaymentRequest request) {

        BigDecimal totalAllocated =
                request.allocations()
                        .stream()
                        .map(PaymentAllocationRequest::amount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        if (totalAllocated.compareTo(request.amount()) != 0) {

            throw new PaymentAllocationException(
                    "Total allocation amount must equal payment amount. "
                            + "Payment: " + request.amount()
                            + ", Allocated: " + totalAllocated
            );
        }
    }

    private void createAllocation(
            Payment payment,
            Customer customer,
            PaymentAllocationRequest request) {

        CultivationJob job =
                cultivationJobRepository
                        .findById(request.jobId())
                        .orElseThrow(() ->
                                new CultivationJobNotFoundException(
                                        request.jobId()
                                ));

        // Make sure the job belongs to the same customer
        if (!job.getCustomer()
                .getCustomerId()
                .equals(customer.getCustomerId())) {

            throw new PaymentAllocationException(
                    "Job " + request.jobId()
                            + " does not belong to customer "
                            + customer.getCustomerId()
            );
        }

        BigDecimal alreadyPaid =
                paymentAllocationRepository
                        .findTotalPaidForJob(
                                job.getJobId()
                        );

        BigDecimal outstanding =
                job.getAmount()
                        .subtract(alreadyPaid);

        if (request.amount().compareTo(outstanding) > 0) {

            throw new PaymentAllocationException(
                    "Allocation of "
                            + request.amount()
                            + " exceeds outstanding amount "
                            + outstanding
                            + " for job "
                            + job.getJobId()
            );
        }

        PaymentAllocation allocation =
                PaymentAllocation.builder()
                        .payment(payment)
                        .job(job)
                        .amount(request.amount())
                        .build();

        paymentAllocationRepository.save(allocation);
    }

    @Override
    public PaymentResponse getPayment(
            Long paymentId) {

        Payment payment =
                paymentRepository.findById(paymentId)
                        .orElseThrow(() ->
                                new PaymentNotFoundException(
                                        paymentId
                                ));

        return mapToResponse(payment);
    }

    @Override
    public Page<PaymentResponse> getPayments(
            Long customerId,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable) {

        return paymentRepository
                .findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public JobBalanceResponse getJobBalance(
            Long jobId) {

        CultivationJob job =
                cultivationJobRepository.findById(jobId)
                        .orElseThrow(() ->
                                new CultivationJobNotFoundException(
                                        jobId
                                ));

        BigDecimal paid =
                paymentAllocationRepository
                        .findTotalPaidForJob(jobId);

        BigDecimal due =
                job.getAmount().subtract(paid);

        return new JobBalanceResponse(
                jobId,
                job.getAmount(),
                paid,
                due
        );
    }

    @Override
    public CustomerBalanceResponse getCustomerBalance(
            Long customerId) {

        Customer customer =
                customerRepository.findById(customerId)
                        .orElseThrow(() ->
                                new CustomerNotFoundException(
                                        customerId
                                ));

        BigDecimal totalAmount =
                cultivationJobRepository
                        .findTotalJobAmountByCustomer(
                                customerId
                        );

        BigDecimal totalPaid =
                paymentAllocationRepository
                        .findTotalPaidForCustomer(
                                customerId
                        );

        BigDecimal due =
                totalAmount.subtract(totalPaid);

        return new CustomerBalanceResponse(
                customerId,
                customer.getName(),
                totalAmount,
                totalPaid,
                due
        );
    }

    private PaymentResponse mapToResponse(
            Payment payment) {

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
