package com.example.demo.repository;

import com.example.demo.entity.CultivationJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
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
}
