package com.raptor.ai.site.domain.model.common;

public enum RiskCriteria {

    VOLATILITY,
    IQR,
    CV,
    IQR_TO_MEDIAN,

    NONE;

    public static RiskCriteria from( final String value) {
        if (value == null) return NONE; // default

        try {
            return RiskCriteria.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return NONE; // safe fallback
        }
    }
}
