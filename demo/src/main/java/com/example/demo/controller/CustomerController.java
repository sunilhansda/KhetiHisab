package com.example.demo.controller;

import com.example.demo.dto.customer.CreateCustomerRequest;
import com.example.demo.dto.customer.CustomerResponse;
import com.example.demo.dto.customer.UpdateCustomerRequest;
import com.example.demo.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {

        CustomerResponse response =
                customerService.createCustomer(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomer(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                customerService.getCustomer(customerId)
        );
    }

    @GetMapping
    public ResponseEntity<Page<CustomerResponse>> getCustomers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String locationCode,
            @PageableDefault(size = 20, sort = "name")
            Pageable pageable) {

        return ResponseEntity.ok(
                customerService.getCustomers(
                        name,
                        locationCode,
                        pageable
                )
        );
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long customerId,
            @Valid @RequestBody UpdateCustomerRequest request) {

        return ResponseEntity.ok(
                customerService.updateCustomer(
                        customerId,
                        request
                )
        );
    }

    @PatchMapping("/{customerId}/status")
    public ResponseEntity<Void> updateCustomerStatus(
            @PathVariable Long customerId,
            @RequestParam boolean active) {

        customerService.updateCustomerStatus(
                customerId,
                active
        );

        return ResponseEntity.noContent().build();
    }
}