package com.innocito.testpilot.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.util.Map;

@Data
public class TestRequestModel {

    @Size(max = 1024, message = "Maximum length of endpointUrl is {max} characters")
    @URL
    @NotBlank(message = "endpointUrl is required")
    private String endpointUrl;

    @NotBlank(message = "method is required")
    private String method;

    //@NotNull(message = "authorization is required")
    private Map<String, String> requestAuthorization;

    private Map<String, String> requestHeaders;

    private Map<String, Object> requestBody;
}