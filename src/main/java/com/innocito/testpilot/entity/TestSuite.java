package com.innocito.testpilot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "testsuite_master")
@Data
@NoArgsConstructor
public class TestSuite extends BaseData implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "testsuite_id")
    private Long id;

    @Column(name = "testsuite_name")
    private String name;

    private String description;

    @Column(name = "project_id")
    private Long projectId;

    @OneToMany(mappedBy = "testSuite")
    @OrderBy("id DESC")
    private List<TestSuiteTestCase> testCases;
}