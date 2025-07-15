package com.innocito.testpilot.model;

import lombok.Data;

@Data
public class RequestResponseUpdateModel {
    private String name;
    private String description;
    private Long repositoryId;
}
