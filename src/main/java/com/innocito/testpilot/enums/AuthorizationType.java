package com.innocito.testpilot.enums;

import com.innocito.testpilot.exception.EnumNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum AuthorizationType {
    No_Auth("No Auth"), Basic("Basic"), Bearer_Token("Bearer Token");
    private String displayValue;

    AuthorizationType(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return this.displayValue;
    }

    public static List<String> getDisplayValues() {
        return Arrays.stream(values()).map(AuthorizationType::getDisplayValue).collect(Collectors.toList());
    }

    public static AuthorizationType getByDisplayValue(String displayValue) {
        for (AuthorizationType type : values()) {
            if (type.getDisplayValue().equalsIgnoreCase(displayValue)) {
                return type;
            }
        }
        throw new EnumNotFoundException("AuthorizationType", "AuthorizationType not found with : " + displayValue);
    }
}