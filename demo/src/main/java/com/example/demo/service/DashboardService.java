package com.example.demo.service;

import com.example.demo.dto.dashboard.DashboardResponse;

import java.time.LocalDate;

public interface DashboardService {

    DashboardResponse getDashboard(
            LocalDate fromDate,
            LocalDate toDate
    );
}
