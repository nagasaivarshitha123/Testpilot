package com.innocito.testpilot.model;

import java.util.Date;

public interface TestSuiteOverallReport {

    Long getTestSuiteId();

    String getTestSuiteName();

    Date getCreationDate();

    Date getLastExecutionDate();

    Integer getTotal();

    Integer getPassed();

    Integer getFailed();
}
