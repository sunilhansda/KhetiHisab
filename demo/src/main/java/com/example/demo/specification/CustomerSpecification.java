package com.example.demo.specification;

import com.example.demo.entity.Customer;
import org.springframework.data.jpa.domain.Specification;

public final class CustomerSpecification {

    private CustomerSpecification() {
    }

    public static Specification<Customer> nameContains(
            String name) {

        return (root, query, cb) ->
                name == null || name.isBlank()
                        ? null
                        : cb.like(
                        cb.lower(root.get("name")),
                        "%" + name.trim().toLowerCase() + "%"
                );
    }

    public static Specification<Customer> locationCodeEquals(
            String locationCode) {

        return (root, query, cb) ->
                locationCode == null || locationCode.isBlank()
                        ? null
                        : cb.equal(
                        cb.upper(root.get("locationCode")),
                        locationCode.trim().toUpperCase()
                );
    }
}
