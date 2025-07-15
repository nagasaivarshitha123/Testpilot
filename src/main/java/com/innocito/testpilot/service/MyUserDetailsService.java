package com.innocito.testpilot.service;

import com.innocito.testpilot.entity.User;
import com.innocito.testpilot.exception.ResourceNotFoundException;
import com.innocito.testpilot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = userOptional.orElseThrow(() ->
                new ResourceNotFoundException("email", "user not found with email"));
        return new org.springframework.security.core.userdetails.User
                (email, user.getOtp(), new ArrayList<>());
    }
}