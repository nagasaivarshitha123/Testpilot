package com.innocito.testpilot.model;

import lombok.Data;

import java.util.Map;

@Data
public class RequestOutput extends BaseDataModel {

    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private Long repositoryId;
    private String endpointUrl;
    private String method;
    private Map<String, String> authorization;
    private Map<String, String> headers;
    private Map<String, Object> body;
}