package com.example.demo.service;

import com.example.demo.dto.driver.*;
import com.example.demo.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface DriverService {

    DriverResponse createDriver(
            CreateDriverRequest request
    );

    DriverResponse getDriver(
            Long driverId
    );

    Page<DriverResponse> getDrivers(
            String name,
            String phone,
            Boolean active,
            Pageable pageable
    );

    DriverResponse updateDriver(
            Long driverId,
            UpdateDriverRequest request
    );

    void updateDriverStatus(
            Long driverId,
            boolean active
    );

    Page<DriverJobResponse> getDriverJobs(
            Long driverId,
            JobStatus status,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable
    );

    DriverSummaryResponse getDriverSummary(
            Long driverId);
}