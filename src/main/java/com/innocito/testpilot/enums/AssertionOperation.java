package com.innocito.testpilot.enums;

import com.innocito.testpilot.exception.EnumNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum AssertionOperation {
    Equals("Equals"), Not_Equals("Not Equals"),
    Less_than("Less than"), Greater_than("Greater than"),
    Contains("Contains"), Not_Contains("Not Contains");
    private String displayValue;

    AssertionOperation(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return this.displayValue;
    }

    public static List<String> getDisplayValues() {
        return Arrays.stream(values()).map(AssertionOperation::getDisplayValue).collect(Collectors.toList());
    }

    public static AssertionOperation getByDisplayValue(String displayValue) {
        for (AssertionOperation type : values()) {
            if (type.getDisplayValue().equalsIgnoreCase(displayValue)) {
                return type;
            }
        }
        throw new EnumNotFoundException("AssertionOperation", "AssertionOperation not found with : " + displayValue);
    }
}