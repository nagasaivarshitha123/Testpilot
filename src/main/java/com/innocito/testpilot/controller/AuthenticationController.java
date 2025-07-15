package com.innocito.testpilot.controller;

import com.innocito.testpilot.config.JwtUtil;
import com.innocito.testpilot.model.AuthRequest;
import com.innocito.testpilot.model.AuthResponse;
import com.innocito.testpilot.model.ResponseModel;
import com.innocito.testpilot.service.UserService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authentication")
@CrossOrigin(allowedHeaders = "*", origins = "*")
public class AuthenticationController {
    private final Logger logger = LogManager.getLogger(AuthenticationController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/verify")
    public ResponseEntity<ResponseModel<AuthResponse>> verifyOtp(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = userService.verifyAndGetAuthResponse(authRequest.getEmail(),
                authRequest.getOtp());
        ResponseModel<AuthResponse> response = ResponseModel.<AuthResponse>builder()
                .data(authResponse)
                .message("User login successful")
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/register-or-login")
    public ResponseEntity<ResponseModel<Void>> registerOrSendOtp(@RequestParam String email) {
        userService.registerOrSendOtp(email);
        ResponseModel<Void> response = ResponseModel.<Void>builder()
                .data(null)
                .message("OTP sent to your email.")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}