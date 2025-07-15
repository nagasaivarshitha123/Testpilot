package com.innocito.testpilot.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

import java.util.Map;

@Data
public class RequestCreationModel {

    @Size(max = 255, message = "Maximum length of name is {max} characters")
    @NotBlank(message = "name is required")
    private String name;

    @Size(max = 1024, message = "Maximum length of description is {max} characters")
    @NotBlank(message = "description is required")
    private String description;

    @NotNull(message = "projectId is required")
    @Range(min = 1)
    private Long projectId;

    @NotNull(message = "repositoryId is required")
    @Range(min = 1)
    private Long repositoryId;

    @Size(max = 1024, message = "Maximum length of endpointUrl is {max} characters")
    @URL
    @NotBlank(message = "endpointUrl is required")
    private String endpointUrl;

    @NotBlank(message = "method is required")
    private String method;

    @NotNull(message = "authorization is required")
    private Map<String, String> authorization;

    private Map<String, String> headers;

    private Map<String, Object> body;
}