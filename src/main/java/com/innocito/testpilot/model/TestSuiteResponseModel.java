package com.innocito.testpilot.model;

import lombok.Data;

import java.util.List;

@Data
public class TestSuiteResponseModel extends BaseDataModel {

    private Long id;

    private String name;

    private String description;

    private Long projectId;

    private List<TestSuiteTestCaseResponseModel> testCases;

    private int testCasesCount;
}