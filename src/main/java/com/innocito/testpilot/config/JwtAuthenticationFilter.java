package com.innocito.testpilot.config;

import com.innocito.testpilot.entity.User;
import com.innocito.testpilot.enums.ActiveStatus;
import com.innocito.testpilot.model.TenantData;
import com.innocito.testpilot.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final Logger logger = LogManager.getLogger(JwtAuthenticationFilter.class);
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TenantData tenantData;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            final String authorizationHeader = request.getHeader("Authorization");

            String userName = null;
            String jwt = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                userName = jwtUtil.extractUsername(jwt);
            }

            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
                User user = userRepository.findByEmail(userName).get();
        //here add me
                tenantData.setLoggedInUserName(user.getName());
                if (ActiveStatus.INACTIVE.getDisplayValue() == user.getActiveStatus()) {
                    logger.info("user with email {} is not active", userName);
                    throw new JwtException("The user trying to log in is not active");
                }

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    tenantData.setLoggedInUserEmail(userName);
                    //tenantData.setLoggedInUserId(user.getId());
                    tenantData.setLoggedInUserId(String.valueOf(user.getId()));

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken
                            (userDetails, null, new ArrayList<>());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (JwtException ex) {
            logger.error("Exception occurred : {}", ex.getMessage());
            response.setStatus(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED.value());
        }
    }
}