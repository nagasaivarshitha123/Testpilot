package com.innocito.testpilot.enums;

import com.innocito.testpilot.exception.EnumNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum AssertionType {
    Status_Code("Status Code"), JSON_Path("JSON Path");
    private String displayValue;

    AssertionType(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return this.displayValue;
    }

    public static List<String> getDisplayValues() {
        return Arrays.stream(values()).map(AssertionType::getDisplayValue).collect(Collectors.toList());
    }

    public static AssertionType getByDisplayValue(String displayValue) {
        for (AssertionType type : values()) {
            if (type.getDisplayValue().equalsIgnoreCase(displayValue)) {
                return type;
            }
        }
        throw new EnumNotFoundException("AssertionType", "AssertionType not found with : " + displayValue);
    }
}