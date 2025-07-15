package com.innocito.testpilot.enums;

import com.innocito.testpilot.exception.EnumNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ReportStatus {
    Passed, Failed;

    public static List<String> getDisplayValues() {
        return Arrays.stream(values()).map(ReportStatus::name).collect(Collectors.toList());
    }

    public static ReportStatus getByDisplayValue(String displayValue) {
        for (ReportStatus type : values()) {
            if (type.name().equalsIgnoreCase(displayValue)) {
                return type;
            }
        }
        throw new EnumNotFoundException("ReportStatus", "ReportStatus not found with : " + displayValue);
    }
}


