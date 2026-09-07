package com.example.demo.repository.projection;

import java.math.BigDecimal;

public interface CustomerJobDueProjection {

    Long getJobId();

    BigDecimal getJobAmount();

    BigDecimal getPaidAmount();
}
