package com.innocito.testpilot.enums;

import com.innocito.testpilot.exception.EnumNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum HttpMethod {
    GET, POST, PUT, DELETE;

    public static List<String> getDisplayValues() {
        return Arrays.stream(values()).map(HttpMethod::name).collect(Collectors.toList());
    }

    public static HttpMethod getByDisplayValue(String displayValue) {
        for (HttpMethod type : values()) {
            if (type.name().equalsIgnoreCase(displayValue)) {
                return type;
            }
        }
        throw new EnumNotFoundException("HttpMethod", "HttpMethod not found with : " + displayValue);
    }
}



