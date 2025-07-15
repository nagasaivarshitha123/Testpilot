package com.innocito.testpilot.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class TestSuiteTestCaseRequestModel {

    @NotNull(message = "testCaseId is required")
    @Range(min = 1)
    private Long testCaseId;

    private boolean enabled;
}