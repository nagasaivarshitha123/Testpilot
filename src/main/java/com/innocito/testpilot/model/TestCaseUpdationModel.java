package com.innocito.testpilot.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class TestCaseUpdationModel {

    @Size(max = 255, message = "Maximum length of name is {max} characters")
    @NotBlank(message = "name is required")
    private String name;

    @Size(max = 1024, message = "Maximum length of description is {max} characters")
    @NotBlank(message = "description is required")
    private String description;

    @Size(max = 1024, message = "Maximum length of endpointUrl is {max} characters")
    @URL
    @NotBlank(message = "endpointUrl is required")
    private String endpointUrl;

    @NotBlank(message = "method is required")
    private String method;
}