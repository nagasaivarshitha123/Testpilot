package com.innocito.testpilot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestSuiteUserReportFilter {
    private Long testSuiteId;
    private Date startDate;
    private Date endDate;
    private String status;
}