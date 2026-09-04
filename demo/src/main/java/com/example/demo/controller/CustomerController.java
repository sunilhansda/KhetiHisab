package com.example.demo.controller;

import com.example.demo.dto.customer.CreateCustomerRequest;
import com.example.demo.dto.customer.CustomerDuesResponse;
import com.example.demo.dto.customer.CustomerResponse;
import com.example.demo.dto.customer.UpdateCustomerRequest;
import com.example.demo.dto.payment.CustomerBalanceResponse;
import com.example.demo.dto.payment.PaymentResponse;
import com.example.demo.service.CustomerService;
import com.example.demo.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Customers", description = "APIs for managing customers")
public class CustomerController {

    private final CustomerService customerService;
    private final PaymentService paymentService;

    @Operation(summary = "Create customer",
            description = "Creates a new customer")
    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        CustomerResponse response = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(summary = "Get customer",
            description = "Returns customer details by ID")
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getCustomer(customerId));
    }

    @Operation(summary = "Get customers",
            description = "Returns customers with optional search and pagination")
    @GetMapping
    public ResponseEntity<Page<CustomerResponse>> getCustomers(@RequestParam(required = false) String name,
                                                               @RequestParam(required = false) String locationCode,
                                                               @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(customerService.getCustomers(name, locationCode, pageable));
    }


    @Operation(summary = "Update customer",
            description = "Updates customer information")
    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable Long customerId,
                                                           @Valid @RequestBody UpdateCustomerRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(customerId, request));
    }

    @Operation(summary = "Update customer status",
            description = "Updates customer status by customer ID")
    @PatchMapping("/{customerId}/status")
    public ResponseEntity<Void> updateCustomerStatus(@PathVariable Long customerId,
                                                     @RequestParam boolean active) {
        customerService.updateCustomerStatus(customerId, active);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get customer balance",
            description = "Get customer balance by customer ID")
    @GetMapping("/{customerId}/balance")
    public ResponseEntity<CustomerBalanceResponse> getCustomerBalance(@PathVariable Long customerId) {
        return ResponseEntity.ok(paymentService.getCustomerBalance(customerId));
    }

    @Operation(summary = "Get customer dues",
            description = "Returns total amount, paid amount and outstanding amount for a customer")
    @GetMapping("/{customerId}/dues")
    public ResponseEntity<CustomerDuesResponse> getCustomerDues (@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getCustomerDues(customerId));
    }

    @Operation(summary = "Get customer payment history",
            description = "Returns paginated payment history for a customer")
    @GetMapping("/{customerId}/payments")
    public ResponseEntity<Page<PaymentResponse>> getCustomerPayments(@PathVariable Long customerId,
                                                                     @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(customerService.getCustomerPayments(customerId, pageable));
    }
}