package com.innocito.testpilot.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TestCaseResponseModel extends BaseDataModel {

    private Long id;

    private String name;

    private String description;

    private Long projectId;

    private Long requestId;

    private String requestName;

    private String repositoryName;

    private String endpointUrl;

    private String method;

    private Map<String, String> authorization;

    private Map<String, String> headers;

    private Map<String, Object> body;

    private List<TestCaseAssertionOutputModel> assertions;

    private List<String> trend;
}