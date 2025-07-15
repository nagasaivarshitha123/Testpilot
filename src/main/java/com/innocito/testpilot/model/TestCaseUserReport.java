package com.innocito.testpilot.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TestCaseUserReport {

    private Long testCaseId;

    private String testCaseName;

    private Date creationDate;

    private Date lastExecutionDate;

    private int total;

    private int passed;

    private int failed;

    private String status;

    private List<TestCaseReportResponseModel> details;
}