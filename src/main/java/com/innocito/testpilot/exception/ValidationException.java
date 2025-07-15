package com.innocito.testpilot.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Map<String, String> errors;
}