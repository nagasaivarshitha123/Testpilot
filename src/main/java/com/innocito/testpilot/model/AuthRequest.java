package com.innocito.testpilot.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequest {
    @Size(max = 250, message = "Maximum length of email is {max} characters")
    @Email
    @NotBlank(message = "email cannot be blank")
    private String email;
    @Size(max = 6, message = "Maximum length of otp is {max} characters")
    @NotBlank(message = "otp cannot be blank")
    private String otp;
}