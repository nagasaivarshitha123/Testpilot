package com.innocito.testpilot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageFilter {
    private int pageNo;
    private int pageSize;
    private String sortBy;
}