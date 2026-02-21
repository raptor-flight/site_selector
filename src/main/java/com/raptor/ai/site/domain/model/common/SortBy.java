package com.raptor.ai.site.domain.model.common;

public enum SortBy {
    MEAN,

    MEDIAN;

    public static SortBy from( final String value) {
        if (value == null) return MEDIAN; // default

        try {
            return SortBy.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return MEDIAN; // safe fallback
        }
    }
}
