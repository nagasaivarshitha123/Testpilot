package com.innocito.testpilot.model;

import lombok.Data;

@Data
public class TestCaseAssertionOutputModel extends BaseDataModel {

    private Long id;

    private String assertionType;

    private String path;

    private String comparison;

    private String value;
}