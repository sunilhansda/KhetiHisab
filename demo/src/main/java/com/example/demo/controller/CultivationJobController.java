package com.example.demo.controller;

import com.example.demo.dto.job.CreateCultivationJobRequest;
import com.example.demo.dto.job.CultivationJobResponse;
import com.example.demo.dto.job.UpdateCultivationJobRequest;
import com.example.demo.dto.payment.JobBalanceResponse;
import com.example.demo.enums.JobStatus;
import com.example.demo.service.CultivationJobService;
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

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Tag(name = "Cultivation Jobs", description = "APIs for managing cultivation jobs")
public class CultivationJobController {

    private final CultivationJobService cultivationJobService;

    @Operation(summary = "Create cultivation job",
            description = "Creates a cultivation job for a customer and assigns a driver")
    @PostMapping
    public ResponseEntity<CultivationJobResponse> createJob(@Valid @RequestBody CreateCultivationJobRequest request) {
        CultivationJobResponse response = cultivationJobService.createJob(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get cultivation job",
            description = "Returns cultivation job details by ID")
    @GetMapping("/{jobId}")
    public ResponseEntity<CultivationJobResponse> getJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(cultivationJobService.getJob(jobId));
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
    @Operation(summary = "Search cultivation jobs",
            description = "Returns cultivation jobs using optional filters")
    @GetMapping
    public ResponseEntity<Page<CultivationJobResponse>> getJobs(@RequestParam(required = false) Long customerId,
                                                                @RequestParam(required = false) Long driverId,
                                                                @RequestParam(required = false) JobStatus status,
                                                                @RequestParam(required = false) LocalDate fromDate,
                                                                @RequestParam(required = false) LocalDate toDate,
                                                                @PageableDefault(size = 20, sort = "jobDate") Pageable pageable) {
        return ResponseEntity.ok(cultivationJobService.getJobs(
                customerId,
                driverId,
                status,
                fromDate,
                toDate,
                pageable));
    }

    @Operation(summary = "Update cultivation job",
            description = "Updates a cultivation job. Job amount cannot be changed after payment allocation.")
    @PutMapping("/{jobId}")
    public ResponseEntity<CultivationJobResponse> updateJob(@PathVariable Long jobId,
                                                            @Valid @RequestBody UpdateCultivationJobRequest request) {
        return ResponseEntity.ok(cultivationJobService.updateJob(jobId, request));
    }

    @Operation(summary = "Update job status",
            description = "Updates the status of a cultivation job")
    @PatchMapping("/{jobId}/status")
    public ResponseEntity<Void> updateJobStatus(@PathVariable Long jobId,
                                                @RequestParam JobStatus status) {
        cultivationJobService.updateJobStatus(jobId, status);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get job balance",
            description = "Returns total job amount, paid amount and outstanding amount")
    @GetMapping("/{jobId}/balance")
    public ResponseEntity<JobBalanceResponse> getJobBalance(@PathVariable Long jobId) {
        return ResponseEntity.ok(cultivationJobService.getJobBalance(jobId));
    }
}
