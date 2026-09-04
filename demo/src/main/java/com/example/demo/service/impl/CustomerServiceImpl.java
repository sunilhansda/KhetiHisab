package com.example.demo.service.impl;

import com.example.demo.dto.customer.CreateCustomerRequest;
import com.example.demo.dto.customer.CustomerDuesResponse;
import com.example.demo.dto.customer.CustomerResponse;
import com.example.demo.dto.customer.UpdateCustomerRequest;
import com.example.demo.dto.payment.JobBalanceResponse;
import com.example.demo.dto.payment.PaymentResponse;
import com.example.demo.entity.CultivationJob;
import com.example.demo.entity.Customer;
import com.example.demo.exception.CustomerNotFoundException;
import com.example.demo.mapper.PaymentMapper;
import com.example.demo.repository.CultivationJobRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.PaymentAllocationRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.service.CustomerService;
import com.example.demo.specification.CustomerSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CultivationJobRepository cultivationJobRepository;
    private final PaymentAllocationRepository paymentAllocationRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public CustomerResponse createCustomer(
            CreateCustomerRequest request) {

        Customer customer = Customer.builder()
                .name(request.name())
                .phone(request.phone())
                .address(request.address())
                .active(true)
                .build();

        Customer savedCustomer = customerRepository.save(customer);

        return mapToResponse(savedCustomer);
    }

    @Override
    public CustomerResponse getCustomer(Long customerId) {

        Customer customer = findCustomer(customerId);

        return mapToResponse(customer);
    }

    @Override
    public Page<CustomerResponse> getCustomers(
            String name,
            String locationCode,
            Pageable pageable) {

        Specification<Customer> specification =
                Specification.allOf(
                        CustomerSpecification.nameContains(name),
                        CustomerSpecification.locationCodeEquals(locationCode)
                );

        return customerRepository
                .findAll(specification, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(
            Long customerId,
            UpdateCustomerRequest request) {

        Customer customer = findCustomer(customerId);

        customer.setName(request.name());
        customer.setPhone(request.phone());
        customer.setAddress(request.address());

        return mapToResponse(customer);
    }

    @Override
    @Transactional
    public void updateCustomerStatus(
            Long customerId,
            boolean active) {

        Customer customer = findCustomer(customerId);

        customer.setActive(active);
    }

    private Customer findCustomer(Long customerId) {

        return customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new CustomerNotFoundException(customerId)
                );
    }

    private CustomerResponse mapToResponse(Customer customer) {

        return new CustomerResponse(
                customer.getCustomerId(),
                customer.getName(),
                customer.getPhone(),
                customer.getAddress(),
                customer.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDuesResponse getCustomerDues(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new CustomerNotFoundException(customerId));

        List<CultivationJob> jobs =
                cultivationJobRepository
                        .findByCustomerCustomerId(customerId);

        List<JobBalanceResponse> jobBalances = jobs.stream()
                .map(job -> {

                    BigDecimal paidAmount =
                            paymentAllocationRepository
                                    .findTotalPaidForJob(
                                            job.getJobId()
                                    );

                    BigDecimal dueAmount =
                            job.getAmount().subtract(paidAmount);

                    return new JobBalanceResponse(
                            job.getJobId(),
                            job.getAmount(),
                            paidAmount,
                            dueAmount
                    );
                })
                .filter(balance ->
                        balance.dueAmount()
                                .compareTo(BigDecimal.ZERO) > 0)
                .toList();

        BigDecimal totalAmount = jobs.stream()
                .map(CultivationJob::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal paidAmount =
                paymentAllocationRepository
                        .findTotalPaidForCustomer(customerId);

        BigDecimal dueAmount =
                totalAmount.subtract(paidAmount);

        return new CustomerDuesResponse(
                customer.getCustomerId(),
                customer.getName(),
                totalAmount,
                paidAmount,
                dueAmount,
                jobBalances
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getCustomerPayments(
            Long customerId,
            Pageable pageable) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new CustomerNotFoundException(customerId));

        return paymentRepository
                .findByCustomerCustomerId(
                        customerId,
                        pageable
                )
                .map(paymentMapper::toResponse);
    }
}
