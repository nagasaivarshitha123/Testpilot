package com.innocito.testpilot.model;

import lombok.Data;

import java.util.List;

@Data
public class TestCaseTestRequestModel extends TestRequestModel {

    private List<TestCaseAssertionModel> requestAssertions;
}