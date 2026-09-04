package com.example.demo.exception;

public class PaymentAllocationException
        extends RuntimeException {

    public PaymentAllocationException(String message) {
        super(message);
    }
}
