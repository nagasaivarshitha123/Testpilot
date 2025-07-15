package com.innocito.testpilot.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PageResponseModel<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int pageNo;
    private int requestedPageSize;
    private long totalElements;
    private int totalPages;
    private int currentPageSize;
    private String message;
    private T items;
}