package com.innocito.testpilot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
public class Project extends BaseData implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Column(name = "project_type")
    private String projectType;

    //added
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id DESC")
    private List<ApiRepository>apiRepositories;

//added me
//@Column(name = "created_by")
//   private String createdBy;
//
//@Column(name = "creation_date")
//@Temporal(TemporalType.TIMESTAMP)
//private Date creationDate;


}