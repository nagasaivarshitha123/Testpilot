package com.innocito.testpilot.enums;

import com.innocito.testpilot.exception.EnumNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum RepositoryType {
    REST, SOAP;

    public static List<String> getDisplayValues() {
        return Arrays.stream(values()).map(RepositoryType::name).collect(Collectors.toList());
    }

    public static RepositoryType getByDisplayValue(String displayValue) {
        for (RepositoryType type : values()) {
            if (type.name().equalsIgnoreCase(displayValue)) {
                return type;
            }
        }
        throw new EnumNotFoundException("RepositoryType", "RepositoryType not found with : " + displayValue);
    }
}