package com.innocito.testpilot.exception;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class ExceptionResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, String> errors;
    private String requestedURI;
}