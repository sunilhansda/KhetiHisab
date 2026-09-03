package com.example.demo.service;

import com.example.demo.dto.job.CreateCultivationJobRequest;
import com.example.demo.dto.job.CultivationJobResponse;
import com.example.demo.dto.job.UpdateCultivationJobRequest;
import com.example.demo.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface CultivationJobService {

    CultivationJobResponse createJob(
            CreateCultivationJobRequest request
    );

    CultivationJobResponse getJob(
            Long jobId
    );

    Page<CultivationJobResponse> getJobs(
            Long customerId,
            Long driverId,
            JobStatus status,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable
    );

    CultivationJobResponse updateJob(
            Long jobId,
            UpdateCultivationJobRequest request
    );

    void updateJobStatus(
            Long jobId,
            JobStatus status
    );
}