package com.innocito.testpilot.model;

import lombok.Data;

import java.util.List;

@Data
public class TestCaseAssertionsOutputModel {

    private List<TestCaseAssertionOutputModel> assertions;
}