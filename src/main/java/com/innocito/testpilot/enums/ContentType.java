package com.innocito.testpilot.enums;

import com.innocito.testpilot.exception.EnumNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ContentType {
    RAW, HTML, XML, JSON;

    public static List<String> getDisplayValues() {
        return Arrays.stream(values()).map(ContentType::name).collect(Collectors.toList());
    }

    public static ContentType getByDisplayValue(String displayValue) {
        for (ContentType type : values()) {
            if (type.name().equalsIgnoreCase(displayValue)) {
                return type;
            }
        }
        throw new EnumNotFoundException("ContentType", "ContentType not found with : " + displayValue);
    }
}