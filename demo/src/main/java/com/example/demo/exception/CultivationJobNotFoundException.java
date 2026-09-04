package com.example.demo.exception;

public class CultivationJobNotFoundException
        extends RuntimeException {

    public CultivationJobNotFoundException(Long jobId) {
        super("Cultivation job not found with id: " + jobId);
    }
}
