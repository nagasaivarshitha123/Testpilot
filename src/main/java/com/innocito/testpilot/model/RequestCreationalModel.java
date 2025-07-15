package com.innocito.testpilot.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

@Data
public class RequestCreationalModel {

    @NotNull(message = "repositoryId is required")
    @Range(min = 1)
    private Long repositoryId;

    @Size(max = 255, message = "Maximum length of name is {max} characters")
    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "method is required")
    private String method;

    @Size(max = 1024, message = "Maximum length of requestyUrl is {max} characters")
    @URL
    @NotBlank(message = "url is required")
    private String url;

    @NotNull(message = "projectId is required")
    @Range(min = 1)
    private Long projectId;

    @Size(max = 1024, message = "Maximum length of requestDescription is {max} characters")
    @NotBlank(message = "description is required")
    private String description;

}
