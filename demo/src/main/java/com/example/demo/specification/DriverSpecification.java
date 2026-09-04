package com.example.demo.specification;

import com.example.demo.entity.Driver;
import org.springframework.data.jpa.domain.Specification;

public final class DriverSpecification {

    private DriverSpecification() {
    }

    public static Specification<Driver> nameContains(
            String name) {

        return (root, query, cb) ->
                name == null || name.isBlank()
                        ? null
                        : cb.like(
                        cb.lower(root.get("name")),
                        "%" + name.trim().toLowerCase() + "%"
                );
    }

    public static Specification<Driver> phoneEquals(
            String phone) {

        return (root, query, cb) ->
                phone == null || phone.isBlank()
                        ? null
                        : cb.equal(
                        root.get("phone"),
                        phone.trim()
                );
    }

    public static Specification<Driver> activeEquals(
            Boolean active) {

        return (root, query, cb) ->
                active == null
                        ? null
                        : cb.equal(
                        root.get("active"),
                        active
                );
    }
}
