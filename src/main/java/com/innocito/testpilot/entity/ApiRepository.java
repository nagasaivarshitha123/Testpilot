package com.innocito.testpilot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "api_repository")
@Data
@NoArgsConstructor
public class ApiRepository extends BaseData implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

//    @Column(name = "project_id")
//    private Long projectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @OneToMany(mappedBy = "apiRepository")
    @OrderBy("id DESC")
    private List<Request> requests;

    @Column(name = "repository_url")
    private String repositoryUrl;

    @Column(name = "repository_type")
    private String repositoryType;
}