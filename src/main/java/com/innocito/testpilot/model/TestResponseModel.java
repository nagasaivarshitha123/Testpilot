package com.innocito.testpilot.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class TestResponseModel {

    @NotBlank(message = "endpointUrl is required")
    private String endpointUrl;

    @NotBlank(message = "method is required")
    private String method;

    //@NotNull(message = "authorization is required")
    private Map<String, String> requestAuthorization;

    private Map<String, String> requestHeaders;

    private Map<String, Object> requestBody;

    private Map<String, String> responseHeaders;

    private String responseBody;

    private Map<String, String> responseMetadata;

    private boolean success;

    private String errorMessage;
}