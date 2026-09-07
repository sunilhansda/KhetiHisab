package com.example.demo.repository;

import com.example.demo.dto.dashboard.DashboardJobSummary;
import com.example.demo.entity.CultivationJob;
import com.example.demo.enums.JobStatus;
import com.example.demo.repository.projection.CustomerJobDueProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface CultivationJobRepository
        extends JpaRepository<CultivationJob, Long>,
        JpaSpecificationExecutor<CultivationJob> {

    @Query("""
    SELECT COALESCE(SUM(j.amount), 0)
    FROM CultivationJob j
    WHERE j.customer.customerId = :customerId
""")
    BigDecimal findTotalJobAmountByCustomer(
            Long customerId
    );

    List<CultivationJob> findByCustomerCustomerId(
            Long customerId
    );

    @Query("""
    SELECT COUNT(j)
    FROM CultivationJob j
    WHERE j.driver.driverId = :driverId
""")
    long countJobsByDriver(Long driverId);

    @Query("""
    SELECT COUNT(j)
    FROM CultivationJob j
    WHERE j.driver.driverId = :driverId
      AND j.status = :status
""")
    long countJobsByDriverAndStatus(
            Long driverId,
            JobStatus status
    );

    @Query("""
    SELECT COALESCE(SUM(j.amount), 0)
    FROM CultivationJob j
    WHERE j.driver.driverId = :driverId
""")
    BigDecimal findTotalAmountByDriver(Long driverId);

    @Query("""
        SELECT new com.khetihisab.dto.dashboard.DashboardJobSummary(
            COUNT(j),
            SUM(CASE
                WHEN j.status = :completed THEN 1
                ELSE 0
            END),
            SUM(CASE
                WHEN j.status = :pending THEN 1
                ELSE 0
            END),
            SUM(CASE
                WHEN j.status = :inProgress THEN 1
                ELSE 0
            END),
            SUM(CASE
                WHEN j.status = :cancelled THEN 1
                ELSE 0
            END),
            COALESCE(SUM(j.amount), 0)
        )
        FROM CultivationJob j
        WHERE (:fromDate IS NULL OR j.jobDate >= :fromDate)
          AND (:toDate IS NULL OR j.jobDate <= :toDate)
    """)
    DashboardJobSummary getDashboardJobSummary(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("completed") JobStatus completed,
            @Param("pending") JobStatus pending,
            @Param("inProgress") JobStatus inProgress,
            @Param("cancelled") JobStatus cancelled
    );

    @Query("""
    SELECT COALESCE(SUM(j.amount), 0)
    FROM CultivationJob j
""")
    BigDecimal findTotalCultivationAmount();

    @Query("""
    SELECT
        j.jobId AS jobId,
        j.amount AS jobAmount,
        COALESCE(SUM(pa.amount), 0) AS paidAmount
    FROM CultivationJob j
    LEFT JOIN PaymentAllocation pa
        ON pa.job = j
    WHERE j.customer.customerId = :customerId
    GROUP BY j.jobId, j.amount
    ORDER BY j.jobId
""")
    List<CustomerJobDueProjection> findCustomerJobDues(
            @Param("customerId") Long customerId
    );
}
