package com.example.demo.repository;

import com.example.demo.dto.customer.JobPaymentSummary;
import com.example.demo.entity.PaymentAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Query("""
    SELECT COALESCE(SUM(pa.amount), 0)
    FROM PaymentAllocation pa
""")
    BigDecimal findTotalAllocatedAmount();

    @Query("""
    SELECT COALESCE(SUM(pa.amount), 0)
    FROM PaymentAllocation pa
    JOIN pa.payment p
    WHERE (:fromDate IS NULL OR p.paymentDate >= :fromDate)
      AND (:toDate IS NULL OR p.paymentDate <= :toDate)
""")
    BigDecimal findReceivedAmountForPeriod(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("""
        SELECT new com.khetihisab.dto.payment.JobPaymentSummary(
            pa.job.jobId,
            COALESCE(SUM(pa.amount), 0)
        )
        FROM PaymentAllocation pa
        WHERE pa.job.jobId IN :jobIds
        GROUP BY pa.job.jobId
    """)
    List<JobPaymentSummary> findPaymentSummaryByJobIds(
            @Param("jobIds") List<Long> jobIds
    );

}
