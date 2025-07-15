package com.innocito.testpilot.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TestSuiteUserReport {

    private Long testSuiteId;

    private String testSuiteName;

    private Date creationDate;

    private Date lastExecutionDate;

    private int total;

    private int passed;

    private int failed;

    private String status;

    private List<TestSuiteReportResponseModel> details;
}