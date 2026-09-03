package com.example.demo.repository;

import com.example.demo.entity.PaymentAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentAllocationRepository
        extends JpaRepository<PaymentAllocation, Long> {

    boolean existsByJobJobId(Long jobId);
}
