package com.example.demo.exception;

public class JobAmountModificationNotAllowedException
        extends RuntimeException {

    public JobAmountModificationNotAllowedException(Long jobId) {
        super(
                "Job amount cannot be modified because payment has already been allocated to job: "
                        + jobId
        );
    }
}