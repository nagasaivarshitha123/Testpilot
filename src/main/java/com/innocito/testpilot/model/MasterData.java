package com.innocito.testpilot.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MasterData implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> httpMethods;
    private List<String> authorizationTypes;
    private List<String> repositoryTypes;
    private List<String> projectTypes;
    private List<String> contentTypes;
    private List<String> assertionTypes;
    private List<String> assertionOperations;
    private List<String> reportStatus;
}