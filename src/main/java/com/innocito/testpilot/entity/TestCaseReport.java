package com.innocito.testpilot.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "testcase_reports")
@Data
@NoArgsConstructor
public class TestCaseReport implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "testcase_run_id")
    private Long id;

    @Column(name = "project_id")
    private Long projectId;

    @ManyToOne
    @JoinColumn(name = "testcase_id", referencedColumnName = "testcase_id")
    private TestCase testCase;

    @Column(name = "endpoint_url")
    private String endpointUrl;

    private String method;

    @Type(JsonType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "request_authorization")
    private Map<String, String> requestAuthorization = new HashMap<>();

    @Type(JsonType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "request_headers")
    private Map<String, String> requestHeaders = new HashMap<>();

    @Type(JsonType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "request_body")
    private Map<String, Object> requestBody = new HashMap<>();

    @Type(JsonType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "response_headers")
    private Map<String, String> responseHeaders = new HashMap<>();

    @Column(name = "response_body")
    private String responseBody;

    @Type(JsonType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "response_metadata")
    private Map<String, String> responseMetadata = new HashMap<>();

    @Column(name = "execution_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date executionDate;

    @ManyToOne
    @JoinColumn(name = "testsuite_report_id")
    private TestSuiteReport testSuiteReport;

    @Column(name = "total_assertions")
    private int totalAssertions;

    @Column(name = "passed_assertions")
    private int passedAssertions;

    @Column(name = "failed_assertions")
    private int failedAssertions;

    @OneToMany(mappedBy = "testCaseReport")
    @OrderBy("id DESC")
    private List<TestCaseAssertionResponse> assertions;

    private String status;
}