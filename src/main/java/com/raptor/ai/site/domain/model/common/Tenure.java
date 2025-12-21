package com.raptor.ai.site.domain.model.common;

public enum Tenure {

    FREEHOLD("F"),
    LEASEHOLD("L");

    private String tenure;

    Tenure ( String tenure) {
        tenure = tenure;
    }

    public String getTenure() {
        return tenure;
    }
}
