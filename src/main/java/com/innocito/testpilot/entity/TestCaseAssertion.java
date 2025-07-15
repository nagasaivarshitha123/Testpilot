package com.innocito.testpilot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "test_case_assertions")
@Data
@NoArgsConstructor
public class TestCaseAssertion extends BaseData implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "testcase_id")
    private TestCase testCase;

    @Column(name = "assertion_type")
    private String assertionType;

    private String path;

    private String comparison;

    private String value;
}