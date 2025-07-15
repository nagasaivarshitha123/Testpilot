package com.innocito.testpilot.model;

import lombok.Data;

import java.util.List;

@Data
public class TestCaseAssertionsExecutionResponse {

    private List<TestCaseAssertionResponseModel> responseAssertions;

    private int totalAssertions;

    private int passedAssertions;

    private int failedAssertions;

    private String status;
}