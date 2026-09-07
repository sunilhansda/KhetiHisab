package com.example.demo.controller;

import com.example.demo.dto.driver.*;
import com.example.demo.enums.JobStatus;
import com.example.demo.service.DriverService;
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
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
@Tag(name = "Drivers", description = "APIs for managing drivers")
public class DriverController {

    private final DriverService driverService;

    @Operation(summary = "Create driver",
            description = "Creates a new driver")
    @PostMapping
    public ResponseEntity<DriverResponse> createDriver(@Valid @RequestBody CreateDriverRequest request) {
        DriverResponse response = driverService.createDriver(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get driver",
            description = "Returns driver details by ID")
    @GetMapping("/{driverId}")
    public ResponseEntity<DriverResponse> getDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(driverService.getDriver(driverId));
    }

    @Operation(summary = "Get drivers",
            description = "Returns drivers with pagination")
    @GetMapping
    public ResponseEntity<Page<DriverResponse>> getDrivers(@RequestParam(required = false) String name,
                                                           @RequestParam(required = false) String phone,
                                                           @RequestParam(required = false) Boolean active,
                                                           @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(driverService.getDrivers(name, phone, active, pageable));
    }

    @Operation(summary = "Update driver",
            description = "Updates driver information")
    @PutMapping("/{driverId}")
    public ResponseEntity<DriverResponse> updateDriver(@PathVariable Long driverId,
                                                       @Valid @RequestBody UpdateDriverRequest request) {
        return ResponseEntity.ok(driverService.updateDriver(driverId, request));
    }

    @Operation(summary = "Update driver status",
            description = "Updates driver information ")
    @PatchMapping("/{driverId}/status")
    public ResponseEntity<Void> updateDriverStatus(@PathVariable Long driverId,
                                                   @RequestParam boolean active) {
        driverService.updateDriverStatus(driverId, active);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get driver's jobs",
            description = "Returns cultivation jobs assigned to a driver")
    @GetMapping("/{driverId}/jobs")
    public ResponseEntity<Page<DriverJobResponse>> getDriverJobs(@PathVariable Long driverId,
                                                                 @RequestParam(required = false) JobStatus status,
                                                                 @RequestParam(required = false) LocalDate fromDate,
                                                                 @RequestParam(required = false) LocalDate toDate,
                                                                 @PageableDefault(size = 20, sort = "jobDate") Pageable pageable) {
        return ResponseEntity.ok(driverService.getDriverJobs(driverId, status, fromDate, toDate, pageable));
    }

    @Operation(summary = "Get driver summary",
            description = "Returns job and cultivation amount summary for a driver")
    @GetMapping("/{driverId}/summary")
    public ResponseEntity<DriverSummaryResponse> getDriverSummary(@PathVariable Long driverId) {
        return ResponseEntity.ok(driverService.getDriverSummary(driverId));
    }

}