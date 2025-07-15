package com.innocito.testpilot.enums;

import com.innocito.testpilot.exception.EnumNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ActiveStatus {
    ACTIVE(1), INACTIVE(0);
    private int displayValue;

    ActiveStatus(int displayValue) {
        this.displayValue = displayValue;
    }

    public int getDisplayValue() {
        return this.displayValue;
    }

    public static List<Integer> getDisplayValues() {
        return Arrays.stream(values()).map(ActiveStatus::getDisplayValue).collect(Collectors.toList());
    }

    public static ActiveStatus getByDisplayValue(int displayValue) {
        for (ActiveStatus type : values()) {
            if (type.getDisplayValue() == displayValue) {
                return type;
            }
        }
        throw new EnumNotFoundException("ActiveStatus", "ActiveStatus not found with : " + displayValue);
    }
}