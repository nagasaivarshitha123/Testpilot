package com.innocito.testpilot.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "testcase_master")
@Data
@NoArgsConstructor
public class TestCase extends BaseData implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "testcase_id")
    private Long id;

    @Column(name = "testcase_name")
    private String name;

    private String description;

    @Column(name = "project_id")
    private Long projectId;

    @ManyToOne
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    private Request request;

    @Column(name = "endpoint_url")
    private String endpointUrl;

    private String method;

    @Type(JsonType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, String> authorization = new HashMap<>();

    @Type(JsonType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, String> headers = new HashMap<>();

    @Type(JsonType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> body = new HashMap<>();

    @OneToMany(mappedBy = "testCase")
    @OrderBy("id DESC")
    private List<TestCaseAssertion> assertions;
}