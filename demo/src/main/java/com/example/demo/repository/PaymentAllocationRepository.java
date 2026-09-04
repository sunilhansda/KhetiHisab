package com.example.demo.repository;

import com.example.demo.entity.PaymentAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentAllocationRepository
        extends JpaRepository<PaymentAllocation, Long> {

    boolean existsByJobJobId(Long jobId);

    List<PaymentAllocation> findByPaymentPaymentId(
            Long paymentId
    );

    @Query("""
        SELECT COALESCE(SUM(pa.amount), 0)
        FROM PaymentAllocation pa
        WHERE pa.job.jobId = :jobId
    """)
    BigDecimal findTotalPaidForJob(Long jobId);

    @Query("""
        SELECT COALESCE(SUM(pa.amount), 0)
        FROM PaymentAllocation pa
        WHERE pa.job.customer.customerId = :customerId
    """)
    BigDecimal findTotalPaidForCustomer(Long customerId);
}
