package com.example.demo.specification;

import com.example.demo.entity.CultivationJob;
import com.example.demo.enums.JobStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class CultivationJobSpecification {

    private CultivationJobSpecification() {
    }

    public static Specification<CultivationJob> customerEquals(
            Long customerId) {

        return (root, query, cb) ->
                customerId == null
                        ? null
                        : cb.equal(
                        root.get("customer").get("customerId"),
                        customerId
                );
    }

    public static Specification<CultivationJob> driverEquals(
            Long driverId) {

        return (root, query, cb) ->
                driverId == null
                        ? null
                        : cb.equal(
                        root.get("driver").get("driverId"),
                        driverId
                );
    }

    public static Specification<CultivationJob> statusEquals(
            JobStatus status) {

        return (root, query, cb) ->
                status == null
                        ? null
                        : cb.equal(root.get("status"), status);
    }

    public static Specification<CultivationJob> dateGreaterThanOrEqual(
            LocalDate fromDate) {

        return (root, query, cb) ->
                fromDate == null
                        ? null
                        : cb.greaterThanOrEqualTo(
                        root.get("jobDate"),
                        fromDate
                );
    }

    public static Specification<CultivationJob> dateLessThanOrEqual(
            LocalDate toDate) {

        return (root, query, cb) ->
                toDate == null
                        ? null
                        : cb.lessThanOrEqualTo(
                        root.get("jobDate"),
                        toDate
                );
    }
}
