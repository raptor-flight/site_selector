package com.raptor.ai.site.domain.model.common;

public enum PropertyType {

    DETACHED("D"),
    SEMI_DETATCHED("S"),
    TERRACED("T"),
    FLATS_MAISONETTES("F"),
    OTHER("O");

    private String propertyType;
    PropertyType(String propertyType) {
        propertyType = propertyType;
    }

    public String getPropertyType() {
        return propertyType;
    }
}
