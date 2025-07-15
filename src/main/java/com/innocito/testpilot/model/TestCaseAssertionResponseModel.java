package com.innocito.testpilot.model;

import lombok.Data;

@Data
public class TestCaseAssertionResponseModel {

    private Long id;

    private String assertionType;

    private String path;

    private String comparison;

    private String value;

    private String actualValue;

    private String status;
}