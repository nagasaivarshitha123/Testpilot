package com.innocito.testpilot.model;

import lombok.Data;

@Data
public class TestSuiteTestCaseResponseModel {

    private Long testCaseId;

    private String testCaseName;

    private String testCaseDescription;

    private boolean enabled;
}