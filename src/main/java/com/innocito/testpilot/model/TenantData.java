package com.innocito.testpilot.model;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Data
@Component
@RequestScope
public class TenantData {
    //private Long loggedInUserId;
    private String loggedInUserId;
    private String loggedInUserEmail;
    private String loggedInUserName;
}