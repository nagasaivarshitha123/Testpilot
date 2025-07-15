package com.innocito.testpilot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

import java.util.Date;

@Data
@MappedSuperclass
public class BaseData {

    @Column(name = "active_status")
    private Integer activeStatus;

    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column(name = "updation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updationDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;
}