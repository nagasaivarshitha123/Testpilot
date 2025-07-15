package com.innocito.testpilot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestCaseFilter {
    private long projectId;
    private String searchText;
}