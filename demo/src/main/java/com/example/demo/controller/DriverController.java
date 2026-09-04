package com.example.demo.controller;

import com.example.demo.dto.driver.CreateDriverRequest;
import com.example.demo.dto.driver.DriverResponse;
import com.example.demo.dto.driver.UpdateDriverRequest;
import com.example.demo.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    public ResponseEntity<DriverResponse> createDriver(
            @Valid @RequestBody CreateDriverRequest request) {

        DriverResponse response =
                driverService.createDriver(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{driverId}")
    public ResponseEntity<DriverResponse> getDriver(
            @PathVariable Long driverId) {

        return ResponseEntity.ok(
                driverService.getDriver(driverId)
        );
    }

    @GetMapping
    public ResponseEntity<Page<DriverResponse>> getDrivers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 20, sort = "name")
            Pageable pageable) {

        return ResponseEntity.ok(
                driverService.getDrivers(
                        name,
                        phone,
                        active,
                        pageable
                )
        );
    }

    @PutMapping("/{driverId}")
    public ResponseEntity<DriverResponse> updateDriver(
            @PathVariable Long driverId,
            @Valid @RequestBody UpdateDriverRequest request) {

        return ResponseEntity.ok(
                driverService.updateDriver(
                        driverId,
                        request
                )
        );
    }

    @PatchMapping("/{driverId}/status")
    public ResponseEntity<Void> updateDriverStatus(
            @PathVariable Long driverId,
            @RequestParam boolean active) {

        driverService.updateDriverStatus(
                driverId,
                active
        );

        return ResponseEntity.noContent().build();
    }


//    TODO: to be implemented later with JobController
//    @GetMapping("/{driverId}/jobs")
//    public ResponseEntity<?> getDriverJobs(
//            @PathVariable Long driverId,
//            Pageable pageable) {
//
//        return ResponseEntity.ok(
//                driverService.getDriverJobs(
//                        driverId,
//                        pageable
//                )
//        );
//    }
//
//    @GetMapping("/{driverId}/summary")
//    public ResponseEntity<?> getDriverSummary(
//            @PathVariable Long driverId) {
//
//        return ResponseEntity.ok(
//                driverService.getDriverSummary(driverId)
//        );
//    }
}