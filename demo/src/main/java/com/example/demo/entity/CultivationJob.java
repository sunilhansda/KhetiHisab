package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cultivation_job")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CultivationJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long jobId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Column(name = "work_type", nullable = false, length = 50)
    private String workType;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal area;

    @Column(name = "area_unit", length = 20)
    private String areaUnit;

    @Column(precision = 12, scale = 2)
    private BigDecimal rate;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    private String location;

    private String description;

    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "job")
    @Builder.Default
    private List<PaymentAllocation> paymentAllocations = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}