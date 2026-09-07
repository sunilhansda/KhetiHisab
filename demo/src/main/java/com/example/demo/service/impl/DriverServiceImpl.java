package com.example.demo.service.impl;

import com.example.demo.dto.driver.*;
import com.example.demo.entity.CultivationJob;
import com.example.demo.entity.Driver;
import com.example.demo.enums.JobStatus;
import com.example.demo.exception.DriverNotFoundException;
import com.example.demo.repository.CultivationJobRepository;
import com.example.demo.repository.DriverRepository;
import com.example.demo.service.DriverService;
import com.example.demo.specification.CultivationJobSpecification;
import com.example.demo.specification.DriverSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final CultivationJobRepository cultivationJobRepository;

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

    @Override
    @Transactional(readOnly = true)
    public Page<DriverJobResponse> getDriverJobs(
            Long driverId,
            JobStatus status,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable) {

        driverRepository.findById(driverId)
                .orElseThrow(() ->
                        new DriverNotFoundException(driverId));

        Specification<CultivationJob> specification =
                CultivationJobSpecification
                        .forDriver(driverId, status, fromDate, toDate);

        return cultivationJobRepository
                .findAll(specification, pageable)
                .map(this::mapToDriverJobResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public DriverSummaryResponse getDriverSummary(
            Long driverId) {

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() ->
                        new DriverNotFoundException(driverId));

        long totalJobs =
                cultivationJobRepository
                        .countJobsByDriver(driverId);

        long completedJobs =
                cultivationJobRepository
                        .countJobsByDriverAndStatus(
                                driverId,
                                JobStatus.COMPLETED
                        );

        long pendingJobs =
                cultivationJobRepository
                        .countJobsByDriverAndStatus(
                                driverId,
                                JobStatus.PENDING
                        );

        long inProgressJobs =
                cultivationJobRepository
                        .countJobsByDriverAndStatus(
                                driverId,
                                JobStatus.IN_PROGRESS
                        );

        long cancelledJobs =
                cultivationJobRepository
                        .countJobsByDriverAndStatus(
                                driverId,
                                JobStatus.CANCELLED
                        );

        BigDecimal totalAmount =
                cultivationJobRepository
                        .findTotalAmountByDriver(driverId);

        return new DriverSummaryResponse(
                driverId,
                driver.getName(),
                totalJobs,
                completedJobs,
                pendingJobs,
                inProgressJobs,
                cancelledJobs,
                totalAmount
        );
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

    private DriverJobResponse mapToDriverJobResponse(CultivationJob job) {
        return new DriverJobResponse(
                job.getJobId(),
                job.getCustomer().getCustomerId(),
                job.getCustomer().getName(),
                job.getCustomer().getAddress(),
                job.getJobDate(),
                job.getAmount(),
                job.getStatus()
        );
    }
}