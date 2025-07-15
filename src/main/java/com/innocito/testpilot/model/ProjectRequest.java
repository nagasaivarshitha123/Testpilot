package com.innocito.testpilot.model;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class ProjectRequest {

    @NotBlank(message = "name is required")
    @Size(max = 255, message = "Maximum length of name is {max} characters")
    private String name;

    @Size(max = 1024, message = "Maximum length of description is {max} characters")
    private String description;

    @Size(max = 255, message = "Maximum length of projectType is {max} characters")
    private String projectType;
}

