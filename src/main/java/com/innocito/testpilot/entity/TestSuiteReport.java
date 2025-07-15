package com.innocito.testpilot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "testsuite_reports_master")
@Data
@NoArgsConstructor
public class TestSuiteReport implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "testsuite_run_id")
    private Long id;

    @Column(name = "project_id")
    private Long projectId;

    @ManyToOne
    @JoinColumn(name = "testsuite_id", referencedColumnName = "testsuite_id")
    private TestSuite testSuite;

    @Column(name = "execution_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date executionDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "enddate")
    private Date endDate;

    @Column(name = "total_test_cases")
    private int totalTestCases;

    @Column(name = "passed_test_cases")
    private int passedTestCases;

    @Column(name = "failed_test_cases")
    private int failedTestCases;

    @OneToMany(mappedBy = "testSuiteReport")
    @OrderBy("id DESC")
    private List<TestCaseReport> testCaseReports;

    private String status;
}