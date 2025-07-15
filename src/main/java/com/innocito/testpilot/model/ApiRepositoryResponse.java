package com.innocito.testpilot.model;

import lombok.Data;

import java.util.List;

@Data
public class ApiRepositoryResponse extends BaseDataModel {

    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private String repositoryUrl;
    private String repositoryType;
    private List<RequestOutput> requests;
}