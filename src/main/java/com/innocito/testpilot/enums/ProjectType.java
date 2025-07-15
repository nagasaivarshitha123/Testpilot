package com.innocito.testpilot.enums;

import com.innocito.testpilot.exception.EnumNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ProjectType {
    Web_Automation("Web Automation"), API_Or_Web_Service("API/Web Service");
    private String displayValue;

    ProjectType(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return this.displayValue;
    }

    public static List<String> getDisplayValues() {
        return Arrays.stream(values()).map(ProjectType::getDisplayValue).collect(Collectors.toList());
    }

    public static ProjectType getByDisplayValue(String displayValue) {
        for (ProjectType type : values()) {
            if (type.getDisplayValue().equalsIgnoreCase(displayValue)) {
                return type;
            }
        }
        throw new EnumNotFoundException("ProjectType", "ProjectType not found with : " + displayValue);
    }
}