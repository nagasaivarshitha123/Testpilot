package com.innocito.testpilot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class BaseDataModel {

    private Integer activeStatus;

    @JsonProperty("createdAt")  // added me
    private Date creationDate;

    @JsonProperty("updatedAt")
    private Date updationDate;



    private String createdBy;

    private String updatedBy;
}