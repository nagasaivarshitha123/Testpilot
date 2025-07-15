package com.innocito.testpilot.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Data
public class TestSuiteCreationModel {

    @Size(max = 255, message = "Maximum length of name is {max} characters")
    @NotBlank(message = "name is required")
    private String name;

    @Size(max = 1024, message = "Maximum length of description is {max} characters")
    @NotBlank(message = "description is required")
    private String description;

    @NotNull(message = "projectId is required")
    @Range(min = 1)
    private Long projectId;

    @Valid
    @NotNull(message = "testCases are required")
    private List<TestSuiteTestCaseRequestModel> testCases;
}