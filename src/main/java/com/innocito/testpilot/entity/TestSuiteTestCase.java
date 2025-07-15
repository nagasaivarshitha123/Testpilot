package com.innocito.testpilot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "test_suite_test_cases")
@Data
@NoArgsConstructor
public class TestSuiteTestCase extends BaseData implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "testsuite_id")
    private TestSuite testSuite;

    @ManyToOne
    @JoinColumn(name = "testcase_id")
    private TestCase testCase;

    private Integer enabled;
}