package com.example.demo.service.impl;

import com.example.demo.dto.dashboard.DashboardJobSummary;
import com.example.demo.dto.dashboard.DashboardResponse;
import com.example.demo.enums.JobStatus;
import com.example.demo.repository.CultivationJobRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.DriverRepository;
import com.example.demo.repository.PaymentAllocationRepository;
import com.example.demo.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl
        implements DashboardService {

    private final CustomerRepository customerRepository;
    private final DriverRepository driverRepository;
    private final CultivationJobRepository
            cultivationJobRepository;
    private final PaymentAllocationRepository
            paymentAllocationRepository;

    @Override
    public DashboardResponse getDashboard(
            LocalDate fromDate,
            LocalDate toDate) {

        validateDateRange(fromDate, toDate);

        DashboardJobSummary jobSummary =
                cultivationJobRepository.getDashboardJobSummary(
                        fromDate,
                        toDate,
                        JobStatus.COMPLETED,
                        JobStatus.PENDING,
                        JobStatus.IN_PROGRESS,
                        JobStatus.CANCELLED
                );

        BigDecimal periodReceivedAmount =
                paymentAllocationRepository
                        .findReceivedAmountForPeriod(
                                fromDate,
                                toDate
                        );

        BigDecimal totalCultivationAmount =
                cultivationJobRepository
                        .findTotalCultivationAmount();

        BigDecimal totalPaidAmount =
                paymentAllocationRepository
                        .findTotalAllocatedAmount();

        BigDecimal totalOutstandingAmount =
                totalCultivationAmount
                        .subtract(totalPaidAmount);

        return new DashboardResponse(
                fromDate,
                toDate,

                customerRepository.countByActiveTrue(),
                driverRepository.countByActiveTrue(),

                jobSummary.totalJobs(),
                jobSummary.completedJobs(),
                jobSummary.pendingJobs(),
                jobSummary.inProgressJobs(),
                jobSummary.cancelledJobs(),

                jobSummary.totalAmount(),
                periodReceivedAmount,
                totalOutstandingAmount
        );
    }

    private void validateDateRange(
            LocalDate fromDate,
            LocalDate toDate) {

        if (fromDate != null
                && toDate != null
                && fromDate.isAfter(toDate)) {

            throw new IllegalArgumentException(
                    "fromDate cannot be after toDate"
            );
        }
    }
}