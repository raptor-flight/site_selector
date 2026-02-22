package com.raptor.ai.site.domain.model.common;

public enum RiskCriteria {
    VOLATILITY,
    IQR,
    CV,
    IQR_TO_MEDIAN,
    NONE;

    public static RiskCriteria from(final String value) {
        if (value == null || value.isBlank()) return NONE;

        try {
            return RiskCriteria.valueOf(value.trim().toUpperCase(java.util.Locale.UK));
        } catch (IllegalArgumentException ex) {
            return NONE;
        }
    }

    public boolean isActive() {
        return this != NONE;
    }
}