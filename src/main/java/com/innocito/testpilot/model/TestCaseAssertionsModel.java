package com.innocito.testpilot.model;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class TestCaseAssertionsModel {

    @Valid
    private List<TestCaseAssertionModel> assertions;
}