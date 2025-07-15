package com.innocito.testpilot.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TestCaseAssertionModel {

    private Long id;

    @NotBlank(message = "assertionType is required")
    private String assertionType;

    private String path;

    @NotBlank(message = "comparison is required")
    private String comparison;

    @NotBlank(message = "value is required")
    private String value;
}