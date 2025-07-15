package com.innocito.testpilot.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

@Data
public class ApiRepositoryCreateRequest {

    @Size(max = 255, message = "Maximum length of name is {max} characters")
    @NotBlank(message = "name is required")
    private String name;

    @Size(max = 1024, message = "Maximum length of description is {max} characters")
    @NotBlank(message = "description is required")
    private String description;

//    @NotNull(message = "projectId is required")
//    @Range(min = 1)
//    private Long projectId;
//added me of projectId
    @NotNull(message = "projectId is required")
    @Range(min = 1, message = "projectId must be at least {min}")
    private Long projectId;

    @Size(max = 1024, message = "Maximum length of repositoryUrl is {max} characters")
    @URL
    @NotBlank(message = "repositoryUrl is required")
    private String repositoryUrl;

    @NotBlank(message = "repositoryType is required")
    private String repositoryType;
}