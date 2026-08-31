package com.example.demo.service;

import com.example.demo.dto.customer.CreateCustomerRequest;
import com.example.demo.dto.customer.CustomerResponse;
import com.example.demo.dto.customer.UpdateCustomerRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    CustomerResponse createCustomer(
            CreateCustomerRequest request
    );

    CustomerResponse getCustomer(
            Long customerId
    );

    Page<CustomerResponse> getCustomers(
            String name,
            String locationCode,
            Pageable pageable
    );

    CustomerResponse updateCustomer(
            Long customerId,
            UpdateCustomerRequest request
    );

    void updateCustomerStatus(
            Long customerId,
            boolean active
    );
}