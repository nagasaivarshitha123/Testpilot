package com.innocito.testpilot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestCaseUserReportFilter {
    private Long testCaseId;
    private Date startDate;
    private Date endDate;
    private String status;
}