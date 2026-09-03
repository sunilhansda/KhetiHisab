package com.example.demo.service.impl;

import com.example.demo.dto.driver.CreateDriverRequest;
import com.example.demo.dto.driver.DriverResponse;
import com.example.demo.dto.driver.UpdateDriverRequest;
import com.example.demo.entity.Driver;
import com.example.demo.exception.DriverNotFoundException;
import com.example.demo.repository.DriverRepository;
import com.example.demo.service.DriverService;
import com.example.demo.specification.DriverSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    @Override
    @Transactional
    public DriverResponse createDriver(
            CreateDriverRequest request) {

        Driver driver = Driver.builder()
                .name(request.name().trim())
                .phone(request.phone())
                .address(request.address())
                .active(true)
                .build();

        Driver savedDriver = driverRepository.save(driver);

        return mapToResponse(savedDriver);
    }

    @Override
    public DriverResponse getDriver(Long driverId) {

        Driver driver = findDriver(driverId);

        return mapToResponse(driver);
    }

    @Override
    public Page<DriverResponse> getDrivers(
            String name,
            String phone,
            Boolean active,
            Pageable pageable) {

        Specification<Driver> specification =
                Specification.allOf(
                        DriverSpecification.nameContains(name),
                        DriverSpecification.phoneEquals(phone),
                        DriverSpecification.activeEquals(active)
                );

        return driverRepository
                .findAll(specification, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public DriverResponse updateDriver(
            Long driverId,
            UpdateDriverRequest request) {

        Driver driver = findDriver(driverId);

        driver.setName(request.name().trim());
        driver.setPhone(request.phone());
        driver.setAddress(request.address());

        return mapToResponse(driver);
    }

    @Override
    @Transactional
    public void updateDriverStatus(
            Long driverId,
            boolean active) {

        Driver driver = findDriver(driverId);

        driver.setActive(active);
    }

    private Driver findDriver(Long driverId) {

        return driverRepository.findById(driverId)
                .orElseThrow(() ->
                        new DriverNotFoundException(driverId)
                );
    }

    private DriverResponse mapToResponse(Driver driver) {

        return new DriverResponse(
                driver.getDriverId(),
                driver.getName(),
                driver.getPhone(),
                driver.getAddress(),
                driver.getActive(),
                driver.getCreatedAt()
        );
    }
}