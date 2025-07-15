package com.innocito.testpilot.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TestSuiteReportResponseModel {

    private Long testSuiteId;

    private Long testSuiteRunId;

    private Long projectId;

    private String name;

    private String description;

    private Date creationDate;

    private Date executionDate;

    private Date endDate;

    private String time;

    private int totalTestCases;

    private int passedTestCases;

    private int failedTestCases;

    private String status;

    private List<TestCaseReportResponseModel> testCasesRunDetails;
}