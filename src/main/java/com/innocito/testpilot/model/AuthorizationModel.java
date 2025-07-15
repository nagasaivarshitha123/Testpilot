package com.innocito.testpilot.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthorizationModel {

    @NotBlank(message = "Authorization type is required")
    private String type;

    private String userName;

    private String password;

    private String token;
}