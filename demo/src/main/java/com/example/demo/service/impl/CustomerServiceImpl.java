package com.example.demo.service.impl;

import com.example.demo.dto.customer.*;
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
import com.example.demo.repository.projection.CustomerJobDueProjection;
import com.example.demo.service.CustomerService;
import com.example.demo.specification.CustomerSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public CustomerDuesResponse getCustomerDues(
            Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new CustomerNotFoundException(customerId));

        List<CustomerJobDueProjection> projections =
                cultivationJobRepository
                        .findCustomerJobDues(customerId);

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalPaidAmount = BigDecimal.ZERO;

        List<CustomerJobDueResponse> jobDues =
                new ArrayList<>();

        for (CustomerJobDueProjection projection : projections) {

            BigDecimal jobAmount =
                    projection.getJobAmount();

            BigDecimal paidAmount =
                    projection.getPaidAmount();

            BigDecimal dueAmount =
                    jobAmount.subtract(paidAmount);

            totalAmount =
                    totalAmount.add(jobAmount);

            totalPaidAmount =
                    totalPaidAmount.add(paidAmount);

            if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {

                jobDues.add(
                        new CustomerJobDueResponse(
                                projection.getJobId(),
                                jobAmount,
                                paidAmount,
                                dueAmount
                        )
                );
            }
        }

        BigDecimal totalDueAmount =
                totalAmount.subtract(totalPaidAmount);

        return new CustomerDuesResponse(
                customer.getCustomerId(),
                customer.getName(),
                totalAmount,
                totalPaidAmount,
                totalDueAmount,
                jobDues
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
