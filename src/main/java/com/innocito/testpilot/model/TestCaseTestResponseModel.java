package com.innocito.testpilot.model;

import lombok.Data;

import java.util.List;

@Data
public class TestCaseTestResponseModel extends TestResponseModel {

    private List<TestCaseAssertionModel> requestAssertions;

    private List<TestCaseAssertionResponseModel> responseAssertions;

    private int totalAssertions;

    private int passedAssertions;

    private int failedAssertions;

    private String status;
}