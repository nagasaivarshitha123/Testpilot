package com.innocito.testpilot.model;

import lombok.Data;

@Data
public class AuthResponse {
    private Long id;
    private String email;
    private String name;
    private String jwt;
}