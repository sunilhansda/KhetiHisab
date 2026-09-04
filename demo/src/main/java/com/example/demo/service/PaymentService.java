package com.example.demo.service;

import com.example.demo.dto.payment.CreatePaymentRequest;
import com.example.demo.dto.payment.CustomerBalanceResponse;
import com.example.demo.dto.payment.JobBalanceResponse;
import com.example.demo.dto.payment.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PaymentService {

    PaymentResponse createPayment(
            CreatePaymentRequest request
    );

    PaymentResponse getPayment(
            Long paymentId
    );

    Page<PaymentResponse> getPayments(
            Long customerId,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable
    );

    CustomerBalanceResponse getCustomerBalance(
            Long customerId
    );

    JobBalanceResponse getJobBalance(
            Long jobId
    );
}
