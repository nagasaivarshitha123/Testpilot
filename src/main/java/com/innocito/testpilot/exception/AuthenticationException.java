package com.innocito.testpilot.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String key;
    private final String message;
}