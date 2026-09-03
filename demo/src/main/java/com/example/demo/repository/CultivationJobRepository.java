package com.example.demo.repository;

import com.example.demo.entity.CultivationJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CultivationJobRepository
        extends JpaRepository<CultivationJob, Long>,
        JpaSpecificationExecutor<CultivationJob> {
}
