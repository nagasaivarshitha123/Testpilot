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
import java.util.Map;

@Entity
@Table(name = "request")
@Data
@NoArgsConstructor
public class Request extends BaseData implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String method;

    @Column(name = "project_id")
    private Long projectId;

    @ManyToOne
    @JoinColumn(name = "repository_id")
    private ApiRepository apiRepository;

    @Column(name = "endpoint_url")
    private String endpointUrl;

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
}