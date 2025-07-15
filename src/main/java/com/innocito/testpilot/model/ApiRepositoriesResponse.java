package com.innocito.testpilot.model;

import lombok.Data;

import java.util.List;

@Data
public class ApiRepositoriesResponse {

   // private List<ApiRepositoryResponse> SOAP;

    private List<ApiRepositoryResponse> REST;

  // private List<ApiRepositoryResponse> repositories; //me added
}