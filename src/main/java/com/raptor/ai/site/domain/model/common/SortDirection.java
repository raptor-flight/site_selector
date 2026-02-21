package com.raptor.ai.site.domain.model.common;

public enum SortDirection {
    ASC, DESC;

    public static SortDirection from(String value) {
        if (value == null) return ASC;
        try {
            return valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            return ASC;
        }
    }
}

