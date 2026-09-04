package com.example.demo.exception;

public class DriverNotFoundException
        extends RuntimeException {

    public DriverNotFoundException(Long driverId) {
        super("Driver not found with id: " + driverId);
    }
}
