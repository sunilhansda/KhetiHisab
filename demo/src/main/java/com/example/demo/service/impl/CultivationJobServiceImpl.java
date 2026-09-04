package com.example.demo.service.impl;

import com.example.demo.dto.job.CreateCultivationJobRequest;
import com.example.demo.dto.job.CultivationJobResponse;
import com.example.demo.dto.job.UpdateCultivationJobRequest;
import com.example.demo.dto.payment.JobBalanceResponse;
import com.example.demo.entity.CultivationJob;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Driver;
import com.example.demo.enums.JobStatus;
import com.example.demo.exception.CultivationJobNotFoundException;
import com.example.demo.exception.CustomerNotFoundException;
import com.example.demo.exception.DriverNotFoundException;
import com.example.demo.exception.JobAmountModificationNotAllowedException;
import com.example.demo.repository.CultivationJobRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.DriverRepository;
import com.example.demo.repository.PaymentAllocationRepository;
import com.example.demo.service.CultivationJobService;
import com.example.demo.specification.CultivationJobSpecification;
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
public class CultivationJobServiceImpl
        implements CultivationJobService {

    private final CultivationJobRepository cultivationJobRepository;
    private final CustomerRepository customerRepository;
    private final DriverRepository driverRepository;
    private final PaymentAllocationRepository paymentAllocationRepository;

    @Override
    @Transactional
    public CultivationJobResponse createJob(
            CreateCultivationJobRequest request) {

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                request.customerId()
                        ));

        Driver driver = driverRepository.findById(request.driverId())
                .orElseThrow(() ->
                        new DriverNotFoundException(
                                request.driverId()
                        ));

        CultivationJob job = CultivationJob.builder()
                .customer(customer)
                .driver(driver)
                .jobDate(request.jobDate())
                .amount(request.amount())
                .status(JobStatus.PENDING)
                .notes(request.notes())
                .build();

        CultivationJob savedJob =
                cultivationJobRepository.save(job);

        return mapToResponse(savedJob);
    }

    @Override
    public CultivationJobResponse getJob(Long jobId) {

        return mapToResponse(findJob(jobId));
    }

    @Override
    public Page<CultivationJobResponse> getJobs(
            Long customerId,
            Long driverId,
            JobStatus status,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable) {

        Specification<CultivationJob> specification =
                Specification.allOf(
                        CultivationJobSpecification
                                .customerEquals(customerId),

                        CultivationJobSpecification
                                .driverEquals(driverId),

                        CultivationJobSpecification
                                .statusEquals(status),

                        CultivationJobSpecification
                                .dateGreaterThanOrEqual(fromDate),

                        CultivationJobSpecification
                                .dateLessThanOrEqual(toDate)
                );

        return cultivationJobRepository
                .findAll(specification, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public CultivationJobResponse updateJob(
            Long jobId,
            UpdateCultivationJobRequest request) {

        CultivationJob job = findJob(jobId);

        Customer customer = customerRepository
                .findById(request.customerId())
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                request.customerId()
                        ));

        Driver driver = driverRepository
                .findById(request.driverId())
                .orElseThrow(() ->
                        new DriverNotFoundException(
                                request.driverId()
                        ));

        // Amount cannot be changed once payment has been allocated
        if (job.getAmount().compareTo(request.amount()) != 0) {

            boolean hasPaymentAllocation =
                    paymentAllocationRepository
                            .existsByJobJobId(jobId);

            if (hasPaymentAllocation) {
                throw new JobAmountModificationNotAllowedException(jobId);
            }

            job.setAmount(request.amount());
        }

        job.setCustomer(customer);
        job.setDriver(driver);
        job.setJobDate(request.jobDate());
        job.setNotes(request.notes());

        return mapToResponse(job);
    }

    @Override
    @Transactional
    public void updateJobStatus(
            Long jobId,
            JobStatus status) {

        CultivationJob job = findJob(jobId);

        job.setStatus(status);
    }

    private CultivationJob findJob(Long jobId) {

        return cultivationJobRepository
                .findById(jobId)
                .orElseThrow(() ->
                        new CultivationJobNotFoundException(jobId));
    }

    private CultivationJobResponse mapToResponse(
            CultivationJob job) {

        return new CultivationJobResponse(
                job.getJobId(),

                job.getCustomer().getCustomerId(),
                job.getCustomer().getName(),

                job.getDriver().getDriverId(),
                job.getDriver().getName(),

                job.getJobDate(),
                job.getAmount(),
                job.getStatus(),
                job.getNotes(),
                job.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public JobBalanceResponse getJobBalance(Long jobId) {

        CultivationJob job = cultivationJobRepository.findById(jobId)
                .orElseThrow(() ->
                        new CultivationJobNotFoundException(jobId));

        BigDecimal paidAmount =
                paymentAllocationRepository.findTotalPaidForJob(jobId);

        BigDecimal dueAmount =
                job.getAmount().subtract(paidAmount);

        return new JobBalanceResponse(
                job.getJobId(),
                job.getAmount(),
                paidAmount,
                dueAmount
        );
    }
}
