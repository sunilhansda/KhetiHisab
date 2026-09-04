package com.example.demo.service;

import com.example.demo.dto.driver.CreateDriverRequest;
import com.example.demo.dto.driver.DriverResponse;
import com.example.demo.dto.driver.UpdateDriverRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
}