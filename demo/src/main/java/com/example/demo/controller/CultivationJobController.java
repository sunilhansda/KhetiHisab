package com.example.demo.controller;

import com.example.demo.dto.job.CreateCultivationJobRequest;
import com.example.demo.dto.job.CultivationJobResponse;
import com.example.demo.dto.job.UpdateCultivationJobRequest;
import com.example.demo.enums.JobStatus;
import com.example.demo.service.CultivationJobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class CultivationJobController {

    private final CultivationJobService cultivationJobService;

    /**
     * Create a new cultivation job.
     */
    @PostMapping
    public ResponseEntity<CultivationJobResponse> createJob(
            @Valid @RequestBody CreateCultivationJobRequest request) {

        CultivationJobResponse response =
                cultivationJobService.createJob(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Get a job by ID.
     */
    @GetMapping("/{jobId}")
    public ResponseEntity<CultivationJobResponse> getJob(
            @PathVariable Long jobId) {

        return ResponseEntity.ok(
                cultivationJobService.getJob(jobId)
        );
    }

    /**
     * Search/filter cultivation jobs with pagination.
     *
     * Examples:
     * /api/v1/jobs
     * /api/v1/jobs?customerId=1
     * /api/v1/jobs?driverId=2
     * /api/v1/jobs?status=COMPLETED
     * /api/v1/jobs?fromDate=2026-09-01&toDate=2026-09-30
     */
    @GetMapping
    public ResponseEntity<Page<CultivationJobResponse>> getJobs(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) JobStatus status,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @PageableDefault(size = 20, sort = "jobDate")
            Pageable pageable) {

        return ResponseEntity.ok(
                cultivationJobService.getJobs(
                        customerId,
                        driverId,
                        status,
                        fromDate,
                        toDate,
                        pageable
                )
        );
    }

    /**
     * Update an existing cultivation job.
     */
    @PutMapping("/{jobId}")
    public ResponseEntity<CultivationJobResponse> updateJob(
            @PathVariable Long jobId,
            @Valid @RequestBody UpdateCultivationJobRequest request) {

        return ResponseEntity.ok(
                cultivationJobService.updateJob(jobId, request)
        );
    }

    /**
     * Update job status.
     *
     * Example:
     * PATCH /api/v1/jobs/10/status?status=COMPLETED
     */
    @PatchMapping("/{jobId}/status")
    public ResponseEntity<Void> updateJobStatus(
            @PathVariable Long jobId,
            @RequestParam JobStatus status) {

        cultivationJobService.updateJobStatus(jobId, status);

        return ResponseEntity.noContent().build();
    }
}
