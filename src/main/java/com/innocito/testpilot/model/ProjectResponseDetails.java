package com.innocito.testpilot.model;


import lombok.Data;


import java.util.List;

@Data

public class ProjectResponseDetails {
    private Long id;
    private String name;
    private String description;
    private String projectType;
    private List<ApiRepositoryResponse> apiRepositories;



}
