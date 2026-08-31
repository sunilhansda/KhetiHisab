package com.example.demo.service.impl;

import com.example.demo.dto.customer.CreateCustomerRequest;
import com.example.demo.dto.customer.CustomerResponse;
import com.example.demo.dto.customer.UpdateCustomerRequest;
import com.example.demo.entity.Customer;
import com.example.demo.exception.CustomerNotFoundException;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.service.CustomerService;
import com.example.demo.specification.CustomerSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

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
}
