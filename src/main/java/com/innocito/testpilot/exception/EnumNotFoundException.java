package com.innocito.testpilot.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EnumNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String key;
    private String message;
}