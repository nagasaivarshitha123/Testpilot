package com.innocito.testpilot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "test_case_assertions_response")
@Data
@NoArgsConstructor
public class TestCaseAssertionResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "testcase_run_id")
    private TestCaseReport testCaseReport;

    @Column(name = "assertion_type")
    private String assertionType;

    private String path;

    private String comparison;

    private String value;

    @Column(name = "actual_value")
    private String actualValue;

    private String status;
}