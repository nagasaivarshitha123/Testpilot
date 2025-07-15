package com.innocito.testpilot.service;

import com.innocito.testpilot.config.JwtUtil;
import com.innocito.testpilot.entity.User;
import com.innocito.testpilot.enums.ActiveStatus;
import com.innocito.testpilot.exception.ValidationException;
import com.innocito.testpilot.model.AuthResponse;
import com.innocito.testpilot.model.TenantData;
import com.innocito.testpilot.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private final Logger logger = LogManager.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private TenantData tenantData;

    @Value("${allowed.domains}")
    private List<String> allowedDomains;
    @Value("${spring.mail.from}")
    private String senderMail;

    @Transactional
    public void registerOrSendOtp(String email) {
        if (!isAllowedDomain(email)) {
            throw new ValidationException(Map.of("email", "Email domain is not allowed."));
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            // Create user if not exists
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(email.split("@")[0]);
            newUser.setIsVerified(ActiveStatus.INACTIVE.getDisplayValue());
            newUser.setActiveStatus(ActiveStatus.ACTIVE.getDisplayValue());
            userRepository.save(newUser);
        }

        // Send OTP
        sendOtp(email);
    }

    private void sendOtp(String email) {
        String otp = generateOtp();
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setOtp(otp);
            user.setOtpGeneratedAt(new Date());
            user.setIsVerified(ActiveStatus.INACTIVE.getDisplayValue());
            userRepository.save(user);

            // Send Email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderMail);
            message.setTo(email);
            message.setSubject("Test Pilot Login OTP");
            message.setText("Dear User, \n Use this OTP to login : " + otp);
            mailSender.send(message);
        }
    }

    private boolean isAllowedDomain(String email) {
        String domain = email.substring(email.indexOf("@") + 1);
        return allowedDomains.contains(domain);
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

//    public Long getLoggedInUser() {
//        return tenantData.getLoggedInUserId();
//    }
public String getLoggedInUser() {
    return tenantData.getLoggedInUserName();
    // assuming this already returns a String
}


    public AuthResponse verifyAndGetAuthResponse(String email, String otp) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getOtp().equals(otp) && user.getIsVerified() == ActiveStatus.INACTIVE.getDisplayValue() &&
                    user.getOtpGeneratedAt().after(new Date(System.currentTimeMillis() - 5 * 60 * 1000))) {
                user.setIsVerified(ActiveStatus.ACTIVE.getDisplayValue());
                userRepository.save(user);
                final String jwt = jwtUtil.generateToken(email);
                AuthResponse authResponse = new AuthResponse();
                authResponse.setId(user.getId());
                authResponse.setName(user.getName());
                authResponse.setEmail(user.getEmail());
                authResponse.setJwt(jwt);
                return authResponse;
            } else {
                throw new ValidationException(Map.of("otp", "Invalid OTP"));
            }
        }
        throw new ValidationException(Map.of("email", "user not found with email"));
    }
}