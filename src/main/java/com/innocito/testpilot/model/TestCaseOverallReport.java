package com.innocito.testpilot.model;

import java.util.Date;

public interface TestCaseOverallReport {

    Long getTestCaseId();

    String getTestCaseName();

    Date getCreationDate();

    Date getLastExecutionDate();

    Integer getTotal();

    Integer getPassed();

    Integer getFailed();
}
