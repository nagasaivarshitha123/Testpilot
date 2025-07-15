package com.innocito.testpilot.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TestCaseReportResponseModel extends TestResponseModel {

    private Long testCaseRunId;

    private Long projectId;

    private Long testCaseId;

    private String testCaseName;

    private String testCaseDescription;

    private Date executionDate;

    private Long testsuiteReportId;

    private List<TestCaseAssertionResponseModel> responseAssertions;

    private int totalAssertions;

    private int passedAssertions;

    private int failedAssertions;

    private String status;
}